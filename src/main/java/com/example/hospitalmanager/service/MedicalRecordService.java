package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.MedicalRecordRequest;
import com.example.hospitalmanager.dto.MedicalRecordResponse;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.MedicalRecord;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public MedicalRecordService(MedicalRecordRepository medicalRecordRepository,
                                 PatientRepository patientRepository,
                                 UserRepository userRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    /** The writing doctor is always taken from the authenticated token's username, never from the request body. */
    public MedicalRecordResponse create(MedicalRecordRequest request, String doctorUsername) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> ApiException.notFound("Patient not found: " + request.patientId()));

        AppUser doctor = userRepository.findByUsername(doctorUsername)
                .orElseThrow(() -> ApiException.notFound("Doctor not found"));

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setDiagnosis(request.diagnosis());
        record.setPrescription(request.prescription());
        record.setCreatedAt(LocalDateTime.now());

        return toResponse(medicalRecordRepository.save(record));
    }

    public List<MedicalRecordResponse> listForPatient(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> ApiException.notFound("Patient not found: " + patientId));
        return medicalRecordRepository.findByPatient(patient).stream().map(this::toResponse).toList();
    }

    private MedicalRecordResponse toResponse(MedicalRecord r) {
        return new MedicalRecordResponse(
                r.getId(),
                r.getPatient().getId(),
                r.getPatient().getFullName(),
                r.getDoctor().getId(),
                r.getDoctor().getFullName(),
                r.getDiagnosis(),
                r.getPrescription(),
                r.getCreatedAt()
        );
    }
}
