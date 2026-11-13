package com.example.hospitalmanager.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long id,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        LocalDateTime appointmentDate,
        String status,
        String reason
) {}
