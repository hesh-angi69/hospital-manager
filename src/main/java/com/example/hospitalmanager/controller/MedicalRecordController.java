package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.MedicalRecordRequest;
import com.example.hospitalmanager.dto.MedicalRecordResponse;
import com.example.hospitalmanager.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** /api/doctors/records — ROLE_DOCTOR writes notes/prescriptions, ROLE_ADMIN can also read. */
@RestController
@RequestMapping("/api/doctors/records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordResponse create(@Valid @RequestBody MedicalRecordRequest request, Authentication authentication) {
        // Doctor identity always comes from the validated token's username, never the request body.
        return medicalRecordService.create(request, authentication.getName());
    }

    @GetMapping("/{patientId}")
    public List<MedicalRecordResponse> forPatient(@PathVariable Long patientId) {
        return medicalRecordService.listForPatient(patientId);
    }
}
