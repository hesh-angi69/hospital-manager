package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.MedicalRecord;
import com.example.hospitalmanager.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatient(Patient patient);
    boolean existsByPatient(Patient patient);
}
