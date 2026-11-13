package com.example.hospitalmanager.config;

import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Patient;
import com.example.hospitalmanager.model.Role;
import com.example.hospitalmanager.repository.PatientRepository;
import com.example.hospitalmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates seeded accounts and sample patients on the first start. */
@Component
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(UserRepository userRepository, PatientRepository patientRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createUserIfMissing("admin", "admin123", "Hospital Admin", Role.ADMIN);
        createUserIfMissing("doctor1", "doctor123", "Dr. Nimal Perera", Role.DOCTOR);
        createUserIfMissing("nurse1", "nurse123", "Nurse Kamala", Role.NURSE);
        createUserIfMissing("reception1", "reception123", "Receptionist Sunil", Role.RECEPTIONIST);

        if (patientRepository.count() == 0) {
            Patient p1 = new Patient();
            p1.setFullName("Saman Kumara");
            p1.setPhoneNumber("0771234567");
            p1.setEmail("saman@example.com");
            p1.setAge(34);
            p1.setAddress("Colombo");
            patientRepository.save(p1);

            Patient p2 = new Patient();
            p2.setFullName("Nimali Silva");
            p2.setPhoneNumber("0719876543");
            p2.setEmail("nimali@example.com");
            p2.setAge(28);
            p2.setAddress("Kelaniya");
            patientRepository.save(p2);
        }

        System.out.println("=============================================");
        System.out.println(" Seeded accounts (username / password):");
        System.out.println("   admin      / admin123      -> ROLE_ADMIN");
        System.out.println("   doctor1    / doctor123      -> ROLE_DOCTOR");
        System.out.println("   nurse1     / nurse123        -> ROLE_NURSE");
        System.out.println("   reception1 / reception123  -> ROLE_RECEPTIONIST");
        System.out.println(" Register your own PATIENT account via /api/auth/register");
        System.out.println("=============================================");
    }

    private void createUserIfMissing(String username, String rawPassword, String fullName, Role role) {
        if (!userRepository.existsByUsername(username)) {
            AppUser user = new AppUser();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setFullName(fullName);
            user.setRole(role);
            userRepository.save(user);
        }
    }
}
