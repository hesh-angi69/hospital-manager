package com.example.hospitalmanager.service;

import com.example.hospitalmanager.dto.AppointmentRequest;
import com.example.hospitalmanager.dto.AppointmentResponse;
import com.example.hospitalmanager.dto.StatusRequest;
import com.example.hospitalmanager.exception.ApiException;
import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Appointment;
import com.example.hospitalmanager.model.AppointmentStatus;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.AppointmentRepository;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.userRepository = userRepository;
    }

    public AppointmentResponse book(AppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> ApiException.notFound("Patient not found: " + request.patientId()));

        AppUser doctor = userRepository.findById(request.doctorId())
                .orElseThrow(() -> ApiException.notFound("Doctor not found: " + request.doctorId()));

        if (doctor.getRole() != Role.DOCTOR) {
            throw ApiException.badRequest("User " + doctor.getUsername() + " is not a doctor");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setReason(request.reason());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment saved = appointmentRepository.save(appointment);
        return toResponse(saved);
    }

    /** Doctors see only their own appointments; admins see all. */
    public List<AppointmentResponse> listForDoctor(String username) {
        AppUser doctor = userRepository.findByUsername(username)
                .orElseThrow(() -> ApiException.notFound("User not found"));

        if (doctor.getRole() == Role.ADMIN) {
            return appointmentRepository.findAll().stream().map(this::toResponse).toList();
        }

        return appointmentRepository.findByDoctor(doctor).stream().map(this::toResponse).toList();
    }

    public AppointmentResponse updateStatus(Long id, StatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Appointment not found: " + id));

        AppointmentStatus status;
        try {
            status = AppointmentStatus.valueOf(request.status().trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw ApiException.badRequest("Invalid status: " + request.status());
        }

        appointment.setStatus(status);
        return toResponse(appointmentRepository.save(appointment));
    }

    private AppointmentResponse toResponse(Appointment a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPatient().getId(),
                a.getPatient().getFullName(),
                a.getDoctor().getId(),
                a.getDoctor().getFullName(),
                a.getAppointmentDate(),
                a.getStatus().name(),
                a.getReason()
        );
    }
}
