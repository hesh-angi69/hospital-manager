package com.example.hospitalmanager.service;

import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.repository.AppointmentRepository;
import com.example.hospitalmanager.repository.MedicalRecordRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    public PatientService(PatientRepository patientRepository,
                           AppointmentRepository appointmentRepository,
                           MedicalRecordRepository medicalRecordRepository) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
    }

    public List<Patient> listAll() {
        return patientRepository.findAll();
    }

    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Patient not found: " + id));
    }

    public Patient create(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient update(Long id, Patient updated) {
        Patient existing = getById(id);
        existing.setFullName(updated.getFullName());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setEmail(updated.getEmail());
        existing.setAge(updated.getAge());
        existing.setAddress(updated.getAddress());
        return patientRepository.save(existing);
    }

    public void delete(Long id) {
        Patient patient = getById(id);
        boolean hasAppointments = appointmentRepository.existsByPatient(patient);
        boolean hasRecords = medicalRecordRepository.existsByPatient(patient);
        if (hasAppointments || hasRecords) {
            throw ApiException.conflict(
                    "Cannot delete patient " + id + ": existing appointments or medical records reference this patient");
        }
        patientRepository.delete(patient);
    }
}
