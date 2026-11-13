package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.AuthResponse;
import com.example.hospitalmanager.dto.LoginRequest;
import com.example.hospitalmanager.dto.RegisterRequest;
import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.UserRepository;
import com.example.hospitalmanager.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        AuthenticationManager authenticationManager,
                        JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw ApiException.conflict("Username already exists: " + request.username());
        }

        Role role;
        try {
            role = Role.valueOf(request.role().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest("Invalid role: " + request.role());
        }

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setRole(role);

        AppUser saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getUsername(), saved.getFullName(), saved.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        AppUser user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> ApiException.notFound("User not found"));

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name()
        );
    }

    public UserResponse me(String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> ApiException.notFound("User not found"));
        return new UserResponse(user.getId(), user.getUsername(), user.getFullName(), user.getRole().name());
    }

    public List<UserResponse> listDoctors() {
        return userRepository.findByRole(Role.DOCTOR).stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getRole().name()))
                .toList();
    }

    public List<UserResponse> listUsers() {
        return userRepository.findAll().stream()
                .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getFullName(), u.getRole().name()))
                .toList();
    }
}
