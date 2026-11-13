package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.dto.StatusRequest;
import com.example.hospitalmanager.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** /api/doctors/appointments — ROLE_DOCTOR (own appointments) and ROLE_ADMIN (all). */
@RestController
@RequestMapping("/api/doctors")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointments")
    public List<AppointmentResponse> myAppointments(Authentication authentication) {
        return appointmentService.listForDoctor(authentication.getName());
    }

    @PatchMapping("/appointments/{id}/status")
    public AppointmentResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusRequest request) {
        return appointmentService.updateStatus(id, request);
    }
}
