package com.example.hospitalmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MedicalRecordRequest(
        @NotNull Long patientId,
        @NotBlank String diagnosis,
        String prescription
) {}
