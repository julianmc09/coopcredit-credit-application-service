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
import static org.mockito.Mockito.when;

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

    private Affiliate testAffiliate;
    private CreditApplication pendingApplication;
    private RiskEvaluation lowRiskResult;
    private RiskEvaluation highRiskResult;

    @BeforeEach
    void setUp() {
        testAffiliate = Affiliate.builder()
                .id("affiliate-1")
                .document("111111111")
                .name("Alice")
                .salary(new BigDecimal("10000.00")) // High salary for approval scenarios
                .affiliationDate(LocalDate.now().minusMonths(7)) // Old enough
                .status(AffiliateStatus.ACTIVE)
                .build();

        pendingApplication = CreditApplication.builder()
                .id("app-1")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("20000.00")) // Reasonable amount
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();

        lowRiskResult = RiskEvaluation.builder()
                .id("risk-1")
                .score(800)
                .riskLevel("BAJO RIESGO")
                .detail("Good credit history.")
                .build();

        highRiskResult = RiskEvaluation.builder()
                .id("risk-2")
                .score(400)
                .riskLevel("ALTO RIESGO")
                .detail("Poor credit history.")
                .build();
    }

    @Test
    @DisplayName("Should approve credit application when all policies are met and risk is low")
    void shouldApproveCreditApplicationSuccessfully() {
        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(testAffiliate.getId())).thenReturn(Optional.of(testAffiliate));
        when(riskEvaluationPort.evaluate(any(), any(), any())).thenReturn(lowRiskResult);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication evaluatedApplication = evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());

        assertNotNull(evaluatedApplication);
        assertEquals(CreditApplicationStatus.APPROVED, evaluatedApplication.getStatus());
        assertNotNull(evaluatedApplication.getRiskEvaluation());
        assertEquals(lowRiskResult.getScore(), evaluatedApplication.getRiskEvaluation().getScore());
        assertTrue(evaluatedApplication.getRiskEvaluation().getReason().contains("approved"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when credit application not found")
    void shouldThrowExceptionWhenCreditApplicationNotFound() {
        when(creditApplicationRepositoryPort.findById(any(String.class))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            evaluateCreditApplicationService.evaluateCreditApplication("non-existent-app");
        });

        assertEquals("Credit Application not found with ID: non-existent-app", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when credit application is not pending")
    void shouldThrowExceptionWhenCreditApplicationIsNotPending() {
        pendingApplication.setStatus(CreditApplicationStatus.APPROVED); // Change status to non-pending
        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());
        });

        assertEquals("Credit Application with ID: app-1 is not in PENDING status.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when affiliate not found for application")
    void shouldThrowExceptionWhenAffiliateNotFoundForApplication() {
        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(testAffiliate.getId())).thenReturn(Optional.empty()); // Simulate affiliate not found

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());
        });

        assertEquals("Affiliate not found for credit application ID: app-1", exception.getMessage());
    }

    @Test
    @DisplayName("Should reject credit application when risk level is high")
    void shouldRejectCreditApplicationWhenRiskLevelIsHigh() {
        when(creditApplicationRepositoryPort.findById(pendingApplication.getId())).thenReturn(Optional.of(pendingApplication));
        when(affiliateRepositoryPort.findById(testAffiliate.getId())).thenReturn(Optional.of(testAffiliate));
        when(riskEvaluationPort.evaluate(any(), any(), any())).thenReturn(highRiskResult);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication evaluatedApplication = evaluateCreditApplicationService.evaluateCreditApplication(pendingApplication.getId());

        assertNotNull(evaluatedApplication);
        assertEquals(CreditApplicationStatus.REJECTED, evaluatedApplication.getStatus());
        assertNotNull(evaluatedApplication.getRiskEvaluation());
        assertEquals(highRiskResult.getScore(), evaluatedApplication.getRiskEvaluation().getScore());
        assertTrue(evaluatedApplication.getRiskEvaluation().getReason().contains("High risk level detected"));
    }

    @Test
    @DisplayName("Should reject credit application when monthly payment exceeds quota/income ratio")
    void shouldRejectCreditApplicationWhenQuotaIncomeRatioExceeded() {
        // Affiliate with lower salary to trigger rejection
        Affiliate lowSalaryAffiliate = testAffiliate.toBuilder().salary(new BigDecimal("1000.00")).build();
        CreditApplication highAmountApplication = pendingApplication.toBuilder().affiliate(lowSalaryAffiliate).requestedAmount(new BigDecimal("5000.00")).term(12).build(); // Monthly payment 5000/12 = 416.67, 30% of 1000 = 300

        when(creditApplicationRepositoryPort.findById(highAmountApplication.getId())).thenReturn(Optional.of(highAmountApplication));
        when(affiliateRepositoryPort.findById(lowSalaryAffiliate.getId())).thenReturn(Optional.of(lowSalaryAffiliate));
        when(riskEvaluationPort.evaluate(any(), any(), any())).thenReturn(lowRiskResult);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication evaluatedApplication = evaluateCreditApplicationService.evaluateCreditApplication(highAmountApplication.getId());

        assertNotNull(evaluatedApplication);
        assertEquals(CreditApplicationStatus.REJECTED, evaluatedApplication.getStatus());
        assertTrue(evaluatedApplication.getRiskEvaluation().getReason().contains("exceeds 30% of salary"));
    }

    @Test
    @DisplayName("Should reject credit application when requested amount exceeds maximum based on salary")
    void shouldRejectCreditApplicationWhenMaxAmountExceeded() {
        // Affiliate with lower salary to trigger rejection
        Affiliate lowSalaryAffiliate = testAffiliate.toBuilder().salary(new BigDecimal("1000.00")).build();
        CreditApplication veryHighAmountApplication = pendingApplication.toBuilder().affiliate(lowSalaryAffiliate).requestedAmount(new BigDecimal("15000.00")).build(); // Max amount 10 * 1000 = 10000

        when(creditApplicationRepositoryPort.findById(veryHighAmountApplication.getId())).thenReturn(Optional.of(veryHighAmountApplication));
        when(affiliateRepositoryPort.findById(lowSalaryAffiliate.getId())).thenReturn(Optional.of(lowSalaryAffiliate));
        when(riskEvaluationPort.evaluate(any(), any(), any())).thenReturn(lowRiskResult);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication evaluatedApplication = evaluateCreditApplicationService.evaluateCreditApplication(veryHighAmountApplication.getId());

        assertNotNull(evaluatedApplication);
        assertEquals(CreditApplicationStatus.REJECTED, evaluatedApplication.getStatus());
        assertTrue(evaluatedApplication.getRiskEvaluation().getReason().contains("exceeds 10 times salary"));
    }

    @Test
    @DisplayName("Should reject credit application when affiliation time is less than minimum")
    void shouldRejectCreditApplicationWhenAffiliationTimeTooShort() {
        // Affiliate with recent affiliation date to trigger rejection
        Affiliate newAffiliate = testAffiliate.toBuilder().affiliationDate(LocalDate.now().minusMonths(3)).build(); // 3 months, less than 6
        CreditApplication newAffiliateApplication = pendingApplication.toBuilder().affiliate(newAffiliate).build();

        when(creditApplicationRepositoryPort.findById(newAffiliateApplication.getId())).thenReturn(Optional.of(newAffiliateApplication));
        when(affiliateRepositoryPort.findById(newAffiliate.getId())).thenReturn(Optional.of(newAffiliate));
        when(riskEvaluationPort.evaluate(any(), any(), any())).thenReturn(lowRiskResult);
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreditApplication evaluatedApplication = evaluateCreditApplicationService.evaluateCreditApplication(newAffiliateApplication.getId());

        assertNotNull(evaluatedApplication);
        assertEquals(CreditApplicationStatus.REJECTED, evaluatedApplication.getStatus());
        assertTrue(evaluatedApplication.getRiskEvaluation().getReason().contains("not met the minimum 6 months of affiliation"));
    }
}
