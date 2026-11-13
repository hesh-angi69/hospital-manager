package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.AppointmentRequest;
import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.service.AppointmentService;
import com.example.hospitalmanager.service.AuthService;
import com.example.hospitalmanager.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * /api/patients/** — patients can view available doctors and book
 * appointments. GET /api/patients (the raw patient record list) is locked
 * down to ROLE_ADMIN with @PreAuthorize, even though the URL-level rule in
 * SecurityConfig lets both PATIENT and ADMIN into this prefix: this is the
 * endpoint used in the JWT-tampering exercise to prove that editing the
 * "role" claim in a token to say ADMIN does NOT grant admin access, because
 * (a) tampering breaks the HS384 signature, and (b) even if it didn't, the
 * server re-reads the real role from the database, never from the token.
 */
@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final AuthService authService;
    private final AppointmentService appointmentService;
    private final PatientService patientService;

    public PatientController(AuthService authService,
                              AppointmentService appointmentService,
                              PatientService patientService) {
        this.authService = authService;
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Patient> listPatientRecords() {
        return patientService.listAll();
    }

    @GetMapping("/doctors")
    public List<UserResponse> availableDoctors() {
        return authService.listDoctors();
    }

    @PostMapping("/appointments")
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        return appointmentService.book(request);
    }
}
