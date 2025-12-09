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
import static org.mockito.Mockito.when;

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
                .id("affiliate-1")
                .document("111111111")
                .name("Alice")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();

        inactiveAffiliate = Affiliate.builder()
                .id("affiliate-2")
                .document("222222222")
                .name("Bob")
                .salary(new BigDecimal("6000.00"))
                .affiliationDate(LocalDate.now().minusMonths(6))
                .status(AffiliateStatus.INACTIVE)
                .build();

        newCreditApplication = CreditApplication.builder()
                .id("app-1")
                .affiliate(activeAffiliate)
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should register a credit application successfully for an active affiliate")
    void shouldRegisterCreditApplicationSuccessfully() {
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));
        when(creditApplicationRepositoryPort.save(any(CreditApplication.class))).thenReturn(newCreditApplication);

        CreditApplication registeredApplication = registerCreditApplicationService.registerCreditApplication(
                activeAffiliate.getId(),
                newCreditApplication.getRequestedAmount(),
                newCreditApplication.getTerm(),
                newCreditApplication.getProposedRate()
        );

        assertNotNull(registeredApplication);
        assertEquals(activeAffiliate.getId(), registeredApplication.getAffiliate().getId());
        assertEquals(CreditApplicationStatus.PENDING, registeredApplication.getStatus());
        assertNotNull(registeredApplication.getApplicationDate());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when affiliate not found")
    void shouldThrowExceptionWhenAffiliateNotFound() {
        when(affiliateRepositoryPort.findById(any(String.class))).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerCreditApplicationService.registerCreditApplication(
                    "non-existent-affiliate",
                    new BigDecimal("10000.00"),
                    24,
                    new BigDecimal("0.05")
            );
        });

        assertEquals("Affiliate not found with ID: non-existent-affiliate", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when affiliate is inactive")
    void shouldThrowExceptionWhenAffiliateIsInactive() {
        when(affiliateRepositoryPort.findById(inactiveAffiliate.getId())).thenReturn(Optional.of(inactiveAffiliate));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerCreditApplicationService.registerCreditApplication(
                    inactiveAffiliate.getId(),
                    new BigDecimal("10000.00"),
                    24,
                    new BigDecimal("0.05")
            );
        });

        assertEquals("Affiliate is not active and cannot apply for a credit.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when requested amount is zero or less")
    void shouldThrowExceptionWhenRequestedAmountIsZeroOrLess() {
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerCreditApplicationService.registerCreditApplication(
                    activeAffiliate.getId(),
                    BigDecimal.ZERO,
                    24,
                    new BigDecimal("0.05")
            );
        });

        assertEquals("Requested amount must be greater than zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when term is zero or less")
    void shouldThrowExceptionWhenTermIsZeroOrLess() {
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerCreditApplicationService.registerCreditApplication(
                    activeAffiliate.getId(),
                    new BigDecimal("10000.00"),
                    0,
                    new BigDecimal("0.05")
            );
        });

        assertEquals("Term must be greater than zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when proposed rate is zero or less")
    void shouldThrowExceptionWhenProposedRateIsZeroOrLess() {
        when(affiliateRepositoryPort.findById(activeAffiliate.getId())).thenReturn(Optional.of(activeAffiliate));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerCreditApplicationService.registerCreditApplication(
                    activeAffiliate.getId(),
                    new BigDecimal("10000.00"),
                    24,
                    BigDecimal.ZERO
            );
        });

        assertEquals("Proposed rate must be greater than zero.", exception.getMessage());
    }
}
