package com.example.hospitalmanager.repository;

import com.example.hospitalmanager.model.AppUser;
import com.example.hospitalmanager.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
    List<AppUser> findByRole(Role role);
}
