package com.example.hospitalmanager.controller;

import com.example.hospitalmanager.dto.UserResponse;
import com.example.hospitalmanager.service.AuthService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** /api/users (admin only) and /api/doctors (any authenticated user can see the doctor list). */
@RestController
public class StaffController {

    private final AuthService authService;

    public StaffController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/api/users")
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return authService.listUsers();
    }

    @GetMapping("/api/doctors")
    public List<UserResponse> listDoctors() {
        return authService.listDoctors();
    }
}
