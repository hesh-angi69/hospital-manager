package com.example.hospitalmanager.dto;

import jakarta.validation.constraints.NotBlank;

public record StatusRequest(
        @NotBlank String status // SCHEDULED, COMPLETED, CANCELLED
) {}
