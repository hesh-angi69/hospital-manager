package com.example.hospitalmanager.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Reads "Authorization: Bearer <token>" on every request.
 *
 * Validation order:
 *  1. Parse + verify the JWT signature and expiry (JwtService). An HS384
 *     signature covers the whole payload, so editing any claim (e.g. the
 *     role) after issuance breaks the signature and this step rejects it.
 *  2. Re-load the user's CURRENT role from the database via
 *     CustomUserDetailsService, instead of trusting the "role" claim in the
 *     token body. This is defense-in-depth: authorization decisions are
 *     always based on what is in the database right now, never on a claim
 *     that travelled inside an (unencrypted, merely signed) JWT.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final ErrorResponseWriter errorResponseWriter;

    public JwtAuthenticationFilter(JwtService jwtService,
                                    UserDetailsService userDetailsService,
                                    ErrorResponseWriter errorResponseWriter) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                     @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException ex) {
            // Signature invalid/tampered, malformed, or expired token.
            SecurityContextHolder.clearContext();
            errorResponseWriter.write(request, response,
                    HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                    "Invalid or expired JWT token");
        }
    }
}
