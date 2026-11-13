package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.RegisterRequest;
import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.service.AuthService;
import com.example.hospitalmanager.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * /api/admin/** — restricted to ROLE_ADMIN by SecurityConfig's
 * authorizeHttpRequests rule (belt) AND by @PreAuthorize here (suspenders).
 * Manage patient and doctor records, view stats, delete entities.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final PatientService patientService;
    private final AuthService authService;

    public AdminController(PatientService patientService, AuthService authService) {
        this.patientService = patientService;
        this.authService = authService;
    }

    @GetMapping("/patients")
    public List<Patient> listPatients() {
        return patientService.listAll();
    }

    @PostMapping("/patients")
    @ResponseStatus(HttpStatus.CREATED)
    public Patient addPatient(@Valid @RequestBody Patient patient) {
        return patientService.create(patient);
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Add a new doctor profile: registers a login account with role DOCTOR. */
    @PostMapping("/doctors")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse addDoctor(@Valid @RequestBody RegisterRequest request) {
        RegisterRequest forcedDoctorRole =
                new RegisterRequest(request.username(), request.password(), request.fullName(), "DOCTOR");
        return authService.register(forcedDoctorRole);
    }

    @GetMapping("/stats")
    public Object stats() {
        return java.util.Map.of(
                "totalPatients", patientService.listAll().size(),
                "totalDoctors", authService.listDoctors().size(),
                "totalUsers", authService.listUsers().size()
        );
    }
}
