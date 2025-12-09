package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.RiskEvaluation;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.RiskEvaluationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EvaluateCreditApplicationServiceTest {

    @Mock
    private CreditApplicationRepositoryPort creditApplicationRepositoryPort;
    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;
    @Mock
    private RiskEvaluationPort riskEvaluationPort;

    @InjectMocks
    private EvaluateCreditApplicationService evaluateCreditApplicationService;

    private Affiliate activeAffiliate;
    private CreditApplication pendingApplication;
    private RiskEvaluation lowRiskEvaluation;

    @BeforeEach
    void setUp() {
        activeAffiliate = Affiliate.builder()
                .id("affiliate-123")
                .document("100000000")
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12)) // 12 months old
                .status(AffiliateStatus.ACTIVE)
                .build();

        pendingApplication = CreditApplication.builder()
                .id("app-456")
                .affiliate(activeAffiliate)
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();

        lowRiskEvaluation = RiskEvaluation.builder()
                .id("risk-789")
                .score(800)
                .riskLevel("BAJO RIESGO")
                .detail("Good credit history")
                .reason("External evaluation: Low Risk")
                .build();
    }

    @Test
    @DisplayName("Should approve credit application when all policies pass and risk is low")
    void shouldApproveCreditApplicationWhenAllPoliciesPassAndRiskIsLow() {
        // Given
        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));
        when(riskEvaluationPort.evaluate(anyString(), any(BigDecimal.class), anyInt())).thenReturn(lowRiskEvaluation);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CreditApplication result = evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());

        // Then
        assertNotNull(result);
        assertEquals(CreditApplicationStatus.APPROVED, result.getStatus());
        assertNotNull(result.getRiskEvaluation());
        assertEquals("Credit application approved based on policies and risk evaluation.", result.getRiskEvaluation().getReason());
        verify(creditApplicationRepositoryPort, times(1)).findById(pendingApplication.getId());
        verify(affiliateRepositoryPort, times(1)).findById(activeAffiliate.getId());
        verify(riskEvaluationPort, times(1)).evaluate(activeAffiliate.getDocument(), pendingApplication.getRequestedAmount(), pendingApplication.getTerm());
        verify(creditApplicationRepositoryPort, times(1)).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should reject credit application when monthly payment exceeds quota/income ratio")
    void shouldRejectCreditApplicationWhenMonthlyPaymentExceedsQuotaIncomeRatio() {
        // Given
        pendingApplication.setRequestedAmount(new BigDecimal("20000.00")); // Makes monthly payment too high
        pendingApplication.setTerm(12); // Monthly payment: 20000/12 = 1666.67. Max allowed: 5000 * 0.30 = 1500

        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));
        when(riskEvaluationPort.evaluate(anyString(), any(BigDecimal.class), anyInt())).thenReturn(lowRiskEvaluation);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CreditApplication result = evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());

        // Then
        assertNotNull(result);
        assertEquals(CreditApplicationStatus.REJECTED, result.getStatus());
        assertNotNull(result.getRiskEvaluation());
        assertTrue(result.getRiskEvaluation().getReason().contains("Monthly payment (1666.67) exceeds 30.00% of salary (1500.00).")); // Corrected line
        verify(creditApplicationRepositoryPort, times(1)).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should reject credit application when requested amount exceeds maximum based on salary")
    void shouldRejectCreditApplicationWhenRequestedAmountExceedsMaxSalaryMultiplier() {
        // Given
        pendingApplication.setRequestedAmount(new BigDecimal("60000.00")); // Max allowed: 5000 * 10 = 50000

        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));
        when(riskEvaluationPort.evaluate(anyString(), any(BigDecimal.class), anyInt())).thenReturn(lowRiskEvaluation);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CreditApplication result = evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());

        // Then
        assertNotNull(result);
        assertEquals(CreditApplicationStatus.REJECTED, result.getStatus());
        assertNotNull(result.getRiskEvaluation());
        assertTrue(result.getRiskEvaluation().getReason().contains("Requested amount (60000.00) exceeds 10 times salary (50000.00)."));
        verify(creditApplicationRepositoryPort, times(1)).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should reject credit application when affiliate has not met minimum affiliation time")
    void shouldRejectCreditApplicationWhenAffiliateHasNotMetMinAffiliationTime() {
        // Given
        activeAffiliate.setAffiliationDate(LocalDate.now().minusMonths(3)); // 3 months old, less than 6
        pendingApplication.setAffiliate(activeAffiliate); // Update affiliate in application

        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));
        when(riskEvaluationPort.evaluate(anyString(), any(BigDecimal.class), anyInt())).thenReturn(lowRiskEvaluation);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CreditApplication result = evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());

        // Then
        assertNotNull(result);
        assertEquals(CreditApplicationStatus.REJECTED, result.getStatus());
        assertNotNull(result.getRiskEvaluation());
        assertTrue(result.getRiskEvaluation().getReason().contains("Affiliate has not met the minimum 6 months of affiliation (currently 3 months)."));
        verify(creditApplicationRepositoryPort, times(1)).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should reject credit application when external risk service returns high risk")
    void shouldRejectCreditApplicationWhenExternalRiskServiceReturnsHighRisk() {
        // Given
        RiskEvaluation highRiskEvaluation = RiskEvaluation.builder()
                .id("risk-999")
                .score(300)
                .riskLevel("ALTO RIESGO")
                .detail("Poor credit history")
                .reason("External evaluation: High Risk")
                .build();

        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));
        when(riskEvaluationPort.evaluate(anyString(), any(BigDecimal.class), anyInt())).thenReturn(highRiskEvaluation);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        CreditApplication result = evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());

        // Then
        assertNotNull(result);
        assertEquals(CreditApplicationStatus.REJECTED, result.getStatus());
        assertNotNull(result.getRiskEvaluation());
        assertTrue(result.getRiskEvaluation().getReason().contains("High risk level detected by external risk central: ALTO RIESGO (Score: 300)."));
        verify(creditApplicationRepositoryPort, times(1)).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when credit application not found")
    void shouldThrowExceptionWhenCreditApplicationNotFound() {
        // Given
        String nonExistentId = "non-existent-id";
        when(creditApplicationRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                evaluateCreditApplicationService.evaluateCreditApplication(nonExistentId)
        );

        assertEquals("Credit Application not found with ID: " + nonExistentId, exception.getMessage());
        verify(affiliateRepositoryPort, never()).findById(anyString());
        verify(riskEvaluationPort, never()).evaluate(anyString(), any(BigDecimal.class), anyInt());
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when credit application is not in PENDING status")
    void shouldThrowExceptionWhenCreditApplicationIsNotPending() {
        // Given
        pendingApplication.setStatus(CreditApplicationStatus.APPROVED); // Not PENDING
        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId())
        );

        assertEquals("Credit Application with ID: app-456 is not in PENDING status.", exception.getMessage());
        verify(affiliateRepositoryPort, never()).findById(anyString());
        verify(riskEvaluationPort, never()).evaluate(anyString(), any(BigDecimal.class), anyInt());
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when affiliate not found for application")
    void shouldThrowExceptionWhenAffiliateNotFoundForApplication() {
        // Given
        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.empty()); // Affiliate not found

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId())
        );

        assertEquals("Affiliate not found for credit application ID: app-456", exception.getMessage());
        verify(riskEvaluationPort, never()).evaluate(anyString(), any(BigDecimal.class), anyInt());
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }
}
