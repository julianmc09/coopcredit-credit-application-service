package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.RegisterUserUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder; // Will be provided by Spring Security configuration

    @Override
    public User registerUser(String username, String password, Role role) {
        // Validate if username already exists
        if (userRepositoryPort.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists: " + username);
        }

        // Encode the password
        String encodedPassword = passwordEncoder.encode(password);

        // Create new user
        User newUser = User.builder()
                .id(UUID.randomUUID().toString())
                .username(username)
                .password(encodedPassword)
                .role(role)
                .build();

        return userRepositoryPort.save(newUser);
    }
}
