package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterCreditApplicationServiceTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;
    @Mock
    private CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @InjectMocks
    private RegisterCreditApplicationService registerCreditApplicationService;

    private Affiliate activeAffiliate;
    private Affiliate inactiveAffiliate;
    private CreditApplication newCreditApplication;

    @BeforeEach
    void setUp() {
        activeAffiliate = Affiliate.builder()
                .id("affiliate-123")
                .document("100000000")
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();

        inactiveAffiliate = Affiliate.builder()
                .id("affiliate-456")
                .document("200000000")
                .name("Jane Doe")
                .salary(new BigDecimal("4000.00"))
                .affiliationDate(LocalDate.now().minusMonths(6))
                .status(AffiliateStatus.INACTIVE)
                .build();

        newCreditApplication = CreditApplication.builder()
                .id("app-789")
                .affiliate(activeAffiliate)
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should register a new credit application successfully for an active affiliate")
    void shouldRegisterNewCreditApplicationSuccessfully() {
        // Given
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenReturn(newCreditApplication);

        // When
        CreditApplication result = registerCreditApplicationService.registerCreditApplication(
                activeAffiliate.getId(),
                newCreditApplication.getRequestedAmount(),
                newCreditApplication.getTerm(),
                newCreditApplication.getProposedRate()
        );

        // Then
        assertNotNull(result);
        assertEquals(newCreditApplication.getId(), result.getId());
        assertEquals(activeAffiliate.getId(), result.getAffiliate().getId());
        assertEquals(CreditApplicationStatus.PENDING, result.getStatus());
        verify(affiliateRepositoryPort, times(1)).findById(activeAffiliate.getId());
        verify(creditApplicationRepositoryPort, times(1)).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when affiliate not found")
    void shouldThrowExceptionWhenAffiliateNotFound() {
        // Given
        String nonExistentAffiliateId = "non-existent-id";
        when(affiliateRepositoryPort.findById(nonExistentAffiliateId)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerCreditApplicationService.registerCreditApplication(
                        nonExistentAffiliateId,
                        newCreditApplication.getRequestedAmount(),
                        newCreditApplication.getTerm(),
                        newCreditApplication.getProposedRate()
                )
        );

        assertEquals("Affiliate not found with ID: " + nonExistentAffiliateId, exception.getMessage());
        verify(affiliateRepositoryPort, times(1)).findById(nonExistentAffiliateId);
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when affiliate is inactive")
    void shouldThrowExceptionWhenAffiliateIsInactive() {
        // Given
        when(affiliateRepositoryPort.findById(inactiveAffiliate.getId())).thenReturn(Optional.of(inactiveAffiliate));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerCreditApplicationService.registerCreditApplication(
                        inactiveAffiliate.getId(),
                        newCreditApplication.getRequestedAmount(),
                        newCreditApplication.getTerm(),
                        newCreditApplication.getProposedRate()
                )
        );

        assertEquals("Affiliate is not active and cannot apply for a credit.", exception.getMessage());
        verify(affiliateRepositoryPort, times(1)).findById(inactiveAffiliate.getId());
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when requested amount is zero or less")
    void shouldThrowExceptionWhenRequestedAmountIsZeroOrLess() {
        // Given
        BigDecimal invalidAmount = BigDecimal.ZERO;
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate)); // Mock affiliate found

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerCreditApplicationService.registerCreditApplication(
                        activeAffiliate.getId(),
                        invalidAmount,
                        newCreditApplication.getTerm(),
                        newCreditApplication.getProposedRate()
                )
        );

        assertEquals("Requested amount must be greater than zero.", exception.getMessage());
        verify(affiliateRepositoryPort, times(1)).findById(activeAffiliate.getId()); // Should try to find affiliate
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when term is zero or less")
    void shouldThrowExceptionWhenTermIsZeroOrLess() {
        // Given
        Integer invalidTerm = 0;
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate)); // Mock affiliate found

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerCreditApplicationService.registerCreditApplication(
                        activeAffiliate.getId(),
                        newCreditApplication.getRequestedAmount(),
                        invalidTerm,
                        newCreditApplication.getProposedRate()
                )
        );

        assertEquals("Term must be greater than zero.", exception.getMessage());
        verify(affiliateRepositoryPort, times(1)).findById(activeAffiliate.getId()); // Should try to find affiliate
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when proposed rate is zero or less")
    void shouldThrowExceptionWhenProposedRateIsZeroOrLess() {
        // Given
        BigDecimal invalidRate = BigDecimal.ZERO;
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate)); // Mock affiliate found

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerCreditApplicationService.registerCreditApplication(
                        activeAffiliate.getId(),
                        newCreditApplication.getRequestedAmount(),
                        newCreditApplication.getTerm(),
                        invalidRate
                )
        );

        assertEquals("Proposed rate must be greater than zero.", exception.getMessage());
        verify(affiliateRepositoryPort, times(1)).findById(activeAffiliate.getId()); // Should try to find affiliate
        verify(creditApplicationRepositoryPort, never()).save(any(CreditApplication.class));
    }
}
