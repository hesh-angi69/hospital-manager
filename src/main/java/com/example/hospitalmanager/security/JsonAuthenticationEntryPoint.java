package com.example.hospitalmanager.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** Sends the 401 JSON response when authentication is missing/invalid. */
@Component
public class JsonAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ErrorResponseWriter errorResponseWriter;

    public JsonAuthenticationEntryPoint(ErrorResponseWriter errorResponseWriter) {
        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        errorResponseWriter.write(request, response,
                HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized",
                "A valid JWT bearer token is required to access this resource");
    }
}
