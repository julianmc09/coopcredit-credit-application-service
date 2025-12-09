package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id("user-1")
                .username("testuser")
                .password("encodedPassword")
                .role(Role.ROLE_AFILIADO)
                .build();
    }

    @Test
    @DisplayName("Should register a user successfully with encoded password")
    void shouldRegisterUserSuccessfully() {
        when(userRepositoryPort.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(userRepositoryPort.save(any(User.class))).thenReturn(testUser);

        User registeredUser = registerUserService.registerUser(
                "newuser",
                "rawPassword",
                Role.ROLE_AFILIADO
        );

        assertNotNull(registeredUser);
        assertEquals("newuser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(Role.ROLE_AFILIADO, registeredUser.getRole());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(userRepositoryPort.findByUsername(any(String.class))).thenReturn(Optional.of(testUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerUserService.registerUser(
                    "testuser",
                    "rawPassword",
                    Role.ROLE_AFILIADO
            );
        });

        assertEquals("Username already exists: testuser", exception.getMessage());
    }
}
