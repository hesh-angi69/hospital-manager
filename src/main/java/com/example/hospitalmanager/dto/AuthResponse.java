package com.example.hospitalmanager.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresInMs,
        String username,
        String fullName,
        String role
) {}
