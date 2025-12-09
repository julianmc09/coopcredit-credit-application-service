package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.LoginRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.RegisterUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use a test profile if you have one for in-memory DB or specific test configs
@Transactional // Rollback changes after each test
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepositoryPort userRepositoryPort; // Use the domain port
    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegisterUserRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Ensure the user does not exist before each test
        userRepositoryPort.findByUsername("testuser").ifPresent(user -> userRepositoryPort.deleteById(user.getId())); // Corrected line

        registerRequest = RegisterUserRequest.builder()
                .username("testuser")
                .password("password123")
                .role(Role.ROLE_AFILIADO)
                .build();

        loginRequest = LoginRequest.builder()
                .username("testuser")
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should register a new user successfully")
    void shouldRegisterNewUserSuccessfully() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("ROLE_AFILIADO"));
    }

    @Test
    @DisplayName("Should not register user if username already exists")
    void shouldNotRegisterUserIfUsernameAlreadyExists() throws Exception {
        // First register the user
        userRepositoryPort.save(User.builder()
                .id("some-id")
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Argument"))
                .andExpect(jsonPath("$.detail").value("Username already exists: testuser"));
    }

    @Test
    @DisplayName("Should log in user successfully and return JWT token")
    void shouldLoginUserSuccessfullyAndReturnJwtToken() throws Exception {
        // Register user first
        userRepositoryPort.save(User.builder()
                .id("some-id")
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("Should not log in with invalid credentials")
    void shouldNotLoginWithInvalidCredentials() throws Exception {
        // Register user first
        userRepositoryPort.save(User.builder()
                .id("some-id")
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(registerRequest.getRole())
                .build());

        LoginRequest invalidLoginRequest = LoginRequest.builder()
                .username("testuser")
                .password("wrongpassword") // Invalid password
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.detail").value("Bad credentials"));
    }
}
