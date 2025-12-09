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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserService registerUserService;

    private String username;
    private String rawPassword;
    private String encodedPassword;
    private Role role;
    private User newUser;

    @BeforeEach
    void setUp() {
        username = "newuser";
        rawPassword = "securepassword";
        encodedPassword = "encoded_securepassword";
        role = Role.ROLE_AFILIADO;

        newUser = User.builder()
                .id("some-uuid")
                .username(username)
                .password(encodedPassword)
                .role(role)
                .build();
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        // Given
        when(userRepositoryPort.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepositoryPort.save(any(User.class))).thenReturn(newUser);

        // When
        User registeredUser = registerUserService.registerUser(username, rawPassword, role);

        // Then
        assertNotNull(registeredUser);
        assertEquals(username, registeredUser.getUsername());
        assertEquals(encodedPassword, registeredUser.getPassword());
        assertEquals(role, registeredUser.getRole());
        verify(userRepositoryPort, times(1)).findByUsername(username);
        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(userRepositoryPort, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // Given
        User existingUser = User.builder().id("existing-uuid").username(username).password("old_encoded").role(Role.ROLE_ADMIN).build();
        when(userRepositoryPort.findByUsername(username)).thenReturn(Optional.of(existingUser));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerUserService.registerUser(username, rawPassword, role)
        );

        assertEquals("Username already exists: " + username, exception.getMessage());
        verify(userRepositoryPort, times(1)).findByUsername(username);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepositoryPort, never()).save(any(User.class));
    }
}
