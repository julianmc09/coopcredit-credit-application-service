package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.*;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.RiskEvaluationPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.UserRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.security.jwt.JwtService;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.CreditApplicationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CreditApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepositoryPort userRepositoryPort;
    @Autowired
    private AffiliateRepositoryPort affiliateRepositoryPort;
    @Autowired
    private CreditApplicationRepositoryPort creditApplicationRepositoryPort;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @MockBean
    private RiskEvaluationPort riskEvaluationPort;

    private String adminToken;
    private String analystToken;
    private String affiliateToken;
    private Affiliate testAffiliate;
    private CreditApplicationRequest creditApplicationRequest;
    private CreditApplication existingApprovedApplication;
    private CreditApplication existingPendingApplication;

    @BeforeEach
    void setUp() {
        userRepositoryPort.deleteAll();
        affiliateRepositoryPort.deleteAll();
        creditApplicationRepositoryPort.deleteAll(); // Ensure credit applications are also cleared

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

        // 2. Create an affiliate for tests
        testAffiliate = Affiliate.builder()
                .id(UUID.randomUUID().toString())
                .document("DOC-" + UUID.randomUUID())
                .name("Test Affiliate")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();
        affiliateRepositoryPort.save(testAffiliate);

        // 3. Prepare a request DTO for new credit application
        creditApplicationRequest = CreditApplicationRequest.builder()
                .affiliateId(testAffiliate.getId())
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .build();

        // 4. Create some existing credit applications for GET tests
        existingApprovedApplication = creditApplicationRepositoryPort.save(CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("12000.00"))
                .term(36)
                .proposedRate(new BigDecimal("0.06"))
                .applicationDate(LocalDate.now().minusDays(5))
                .status(CreditApplicationStatus.APPROVED)
                .build());

        existingPendingApplication = creditApplicationRepositoryPort.save(CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("8000.00"))
                .term(12)
                .proposedRate(new BigDecimal("0.07"))
                .applicationDate(LocalDate.now().minusDays(2))
                .status(CreditApplicationStatus.PENDING)
                .build());
    }

    @Test
    @DisplayName("Affiliate should be able to register a new credit application")
    void affiliateShouldBeAbleToRegisterNewCreditApplication() throws Exception {
        mockMvc.perform(post("/credit-applications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creditApplicationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.affiliate.id").value(testAffiliate.getId()));
    }

    @Test
    @DisplayName("Analyst should not be able to register a new credit application")
    void analystShouldNotBeAbleToRegisterNewCreditApplication() throws Exception {
        mockMvc.perform(post("/credit-applications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creditApplicationRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Analyst should be able to evaluate a pending credit application")
    void analystShouldBeAbleToEvaluatePendingApplication() throws Exception {
        // Mock the external risk evaluation service
        RiskEvaluation lowRiskEvaluation = RiskEvaluation.builder()
                .score(800)
                .riskLevel("BAJO RIESGO")
                .detail("Good credit history")
                .build();
        when(riskEvaluationPort.evaluate(any(), any(), anyInt())).thenReturn(lowRiskEvaluation);

        // Perform the evaluation
        mockMvc.perform(post("/credit-applications/{id}/evaluate", existingPendingApplication.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingPendingApplication.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.riskEvaluation").exists())
                .andExpect(jsonPath("$.riskEvaluation.score").value(800));
    }

    @Test
    @DisplayName("Affiliate should not be able to evaluate a credit application")
    void affiliateShouldNotBeAbleToEvaluateApplication() throws Exception {
        mockMvc.perform(post("/credit-applications/{id}/evaluate", existingPendingApplication.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin should be able to get any credit application by ID")
    void adminShouldBeAbleToGetAnyCreditApplicationById() throws Exception {
        mockMvc.perform(get("/credit-applications/{id}", existingApprovedApplication.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingApprovedApplication.getId()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Analyst should be able to get any credit application by ID")
    void analystShouldBeAbleToGetAnyCreditApplicationById() throws Exception {
        mockMvc.perform(get("/credit-applications/{id}", existingPendingApplication.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingPendingApplication.getId()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Affiliate should be able to get their own credit application by ID")
    void affiliateShouldBeAbleToGetTheirOwnCreditApplicationById() throws Exception {
        // Create an application specifically for the testAffiliate
        CreditApplication affiliateOwnedApplication = creditApplicationRepositoryPort.save(CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("5000.00"))
                .term(12)
                .proposedRate(new BigDecimal("0.06"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build());

        mockMvc.perform(get("/credit-applications/{id}", affiliateOwnedApplication.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(affiliateOwnedApplication.getId()));
    }

    @Test
    @DisplayName("Affiliate should not be able to get another affiliate's credit application by ID")
    void affiliateShouldNotBeAbleToGetAnotherAffiliatesCreditApplicationById() throws Exception {
        // Create another affiliate and their application
        Affiliate otherAffiliate = Affiliate.builder()
                .id(UUID.randomUUID().toString())
                .document("OTHERDOC-" + UUID.randomUUID())
                .name("Other Affiliate")
                .salary(new BigDecimal("6000.00"))
                .affiliationDate(LocalDate.now().minusMonths(5))
                .status(AffiliateStatus.ACTIVE)
                .build();
        affiliateRepositoryPort.save(otherAffiliate);

        CreditApplication otherAffiliateApplication = creditApplicationRepositoryPort.save(CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .affiliate(otherAffiliate)
                .requestedAmount(new BigDecimal("15000.00"))
                .term(36)
                .proposedRate(new BigDecimal("0.04"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.APPROVED)
                .build());

        mockMvc.perform(get("/credit-applications/{id}", otherAffiliateApplication.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken)) // Affiliate trying to access other's app
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Admin should be able to get all credit applications")
    void adminShouldBeAbleToGetAllCreditApplications() throws Exception {
        mockMvc.perform(get("/credit-applications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // Two applications created in setUp
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[1].id").exists());
    }

    @Test
    @DisplayName("Analyst should not be able to get all credit applications")
    void analystShouldNotBeAbleToGetAllCreditApplications() throws Exception {
        mockMvc.perform(get("/credit-applications")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Analyst should be able to get all pending credit applications")
    void analystShouldBeAbleToGetAllPendingCreditApplications() throws Exception {
        mockMvc.perform(get("/credit-applications/pending")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + analystToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // Only existingPendingApplication is pending
                .andExpect(jsonPath("$[0].id").value(existingPendingApplication.getId()))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("Admin should not be able to get all pending credit applications")
    void adminShouldNotBeAbleToGetAllPendingCreditApplications() throws Exception {
        mockMvc.perform(get("/credit-applications/pending")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @DisplayName("Affiliate should not be able to get all pending credit applications")
    void affiliateShouldNotBeAbleToGetAllPendingCreditApplications() throws Exception {
        mockMvc.perform(get("/credit-applications/pending")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + affiliateToken))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }
}
