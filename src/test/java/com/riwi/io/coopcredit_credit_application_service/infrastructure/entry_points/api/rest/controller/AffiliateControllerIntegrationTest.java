package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.security.jwt.JwtService;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.AffiliateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Testcontainers
class AffiliateControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepositoryPort userRepositoryPort;
    @Autowired
    private AffiliateRepositoryPort affiliateRepositoryPort;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    private String adminToken;
    private String analystToken;
    private String affiliateToken;
    private Affiliate existingAffiliate;
    private AffiliateRequest newAffiliateRequest;

    @BeforeEach
    void setUp() {
        // Clear repositories for isolation (Transactional will rollback, but explicit clear helps)
        userRepositoryPort.deleteAll();
        affiliateRepositoryPort.deleteAll();

        // 1. Register users and generate tokens
        String adminUsername = "admin-" + UUID.randomUUID();
        String analystUsername = "analyst-" + UUID.randomUUID();
        String affiliateUsername = "affiliate-" + UUID.randomUUID();
        String password = "password123";

        User adminUser = User.builder().id(UUID.randomUUID().toString()).username(adminUsername).password(passwordEncoder.encode(password)).role(Role.ROLE_ADMIN).build();
        User analystUser = User.builder().id(UUID.randomUUID().toString()).username(analystUsername).password(passwordEncoder.encode(password)).role(Role.ROLE_ANALISTA).build();
        User regularAffiliateUser = User.builder().id(UUID.randomUUID().toString()).username(affiliateUsername).password(passwordEncoder.encode(password)).role(Role.ROLE_AFILIADO).build();

        userRepositoryPort.save(adminUser);
        userRepositoryPort.save(analystUser);
        userRepositoryPort.save(regularAffiliateUser);

        UserDetails adminUserDetails = userDetailsService.loadUserByUsername(adminUsername);
        UserDetails analystUserDetails = userDetailsService.loadUserByUsername(analystUsername);
        UserDetails affiliateUserDetails = userDetailsService.loadUserByUsername(affiliateUsername);

        adminToken = jwtService.generateToken(adminUserDetails);
        analystToken = jwtService.generateToken(analystUserDetails);
        affiliateToken = jwtService.generateToken(affiliateUserDetails);

        // 2. Create an existing affiliate for update/get tests
        existingAffiliate = Affiliate.builder()
                .id(UUID.randomUUID().toString())
                .document("DOC-" + UUID.randomUUID()) // Unique document
                .name("Existing Affiliate")
                .salary(new BigDecimal("2500.00"))
                .affiliationDate(LocalDate.now().minusMonths(10))
                .status(AffiliateStatus.ACTIVE)
                .build();
        affiliateRepositoryPort.save(existingAffiliate);

        // 3. Prepare a request DTO for new affiliate creation with unique document
        newAffiliateRequest = AffiliateRequest.builder()
                .document("NEWDOC-" + UUID.randomUUID()) // Unique document
                .name("New Affiliate")
                .salary(new BigDecimal("3000.00"))
                .affiliationDate(LocalDate.now())
                .status(AffiliateStatus.ACTIVE)
                .build();
    }

    // --- Security Tests ---

    @Test
    @DisplayName("Admin should be able to register a new affiliate")
    void adminShouldBeAbleToRegisterNewAffiliate() throws Exception {
        mockMvc.perform(post("/affiliates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAffiliateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.document").value(newAffiliateRequest.getDocument()));
    }

    @Test
    @DisplayName("Analyst should not be able to register a new affiliate")
    void analystShouldNotBeAbleToRegisterNewAffiliate() throws Exception {
        mockMvc.perform(post("/affiliates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAffiliateRequest)))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Affiliate should not be able to register a new affiliate")
    void affiliateShouldNotBeAbleToRegisterNewAffiliate() throws Exception {
        mockMvc.perform(post("/affiliates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAffiliateRequest)))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Unauthenticated user should not be able to register a new affiliate")
    void unauthenticatedUserShouldNotBeAbleToRegisterNewAffiliate() throws Exception {
        mockMvc.perform(post("/affiliates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newAffiliateRequest)))
                .andExpect(status().isUnauthorized()); // 401 Unauthorized
    }

    // --- CRUD Tests ---

    @Test
    @DisplayName("Admin should be able to get an affiliate by ID")
    void adminShouldBeAbleToGetAffiliateById() throws Exception {
        mockMvc.perform(get("/affiliates/{id}", existingAffiliate.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingAffiliate.getId()))
                .andExpect(jsonPath("$.name").value(existingAffiliate.getName()));
    }

    @Test
    @DisplayName("Analyst should be able to get an affiliate by ID")
    void analystShouldBeAbleToGetAffiliateById() throws Exception {
        mockMvc.perform(get("/affiliates/{id}", existingAffiliate.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingAffiliate.getId()))
                .andExpect(jsonPath("$.name").value(existingAffiliate.getName()));
    }

    @Test
    @DisplayName("Affiliate should not be able to get an affiliate by ID")
    void affiliateShouldNotBeAbleToGetAffiliateById() throws Exception {
        mockMvc.perform(get("/affiliates/{id}", existingAffiliate.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Admin should be able to update an existing affiliate")
    void adminShouldBeAbleToUpdateExistingAffiliate() throws Exception {
        AffiliateRequest updateRequest = AffiliateRequest.builder()
                .document(existingAffiliate.getDocument()) // Document remains the same for update
                .name("Updated Name")
                .salary(new BigDecimal("3500.00"))
                .affiliationDate(existingAffiliate.getAffiliationDate())
                .status(AffiliateStatus.INACTIVE)
                .build();

        mockMvc.perform(put("/affiliates/{id}", existingAffiliate.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingAffiliate.getId()))
                .andExpect(jsonPath("$.name").value(updateRequest.getName()))
                .andExpect(jsonPath("$.salary").value("3500.0")) // Corrected here
                .andExpect(jsonPath("$.status").value(updateRequest.getStatus().name()));
    }

    @Test
    @DisplayName("Analyst should not be able to update an affiliate")
    void analystShouldNotBeAbleToUpdateAffiliate() throws Exception {
        AffiliateRequest updateRequest = AffiliateRequest.builder()
                .document(existingAffiliate.getDocument())
                .name("Updated Name")
                .salary(new BigDecimal("3500.00"))
                .affiliationDate(existingAffiliate.getAffiliationDate())
                .status(AffiliateStatus.INACTIVE)
                .build();

        mockMvc.perform(put("/affiliates/{id}", existingAffiliate.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Admin should be able to get all affiliates")
    void adminShouldBeAbleToGetAllAffiliates() throws Exception {
        mockMvc.perform(get("/affiliates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Only existingAffiliate is in DB initially
                .andExpect(jsonPath("$[0].id").value(existingAffiliate.getId()));
    }

    @Test
    @DisplayName("Analyst should be able to get all affiliates")
    void analystShouldBeAbleToGetAllAffiliates() throws Exception {
        mockMvc.perform(get("/affiliates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(existingAffiliate.getId()));
    }

    @Test
    @DisplayName("Affiliate should not be able to get all affiliates")
    void affiliateShouldNotBeAbleToGetAllAffiliates() throws Exception {
        mockMvc.perform(get("/affiliates")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }
}
