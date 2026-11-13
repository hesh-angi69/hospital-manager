package com.example.hospitalmanager.dto;

public record UserResponse(
        Long id,
        String username,
        String fullName,
        String role
) {}
