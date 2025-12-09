package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus; // Added this import
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCreditApplicationServiceTest {

    @Mock
    private CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @InjectMocks
    private GetCreditApplicationService getCreditApplicationService;

    private CreditApplication testCreditApplication;

    @BeforeEach
    void setUp() {
        Affiliate affiliate = Affiliate.builder()
                .id("affiliate-123")
                .document("100000000")
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();

        testCreditApplication = CreditApplication.builder()
                .id("app-456")
                .affiliate(affiliate)
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should return credit application by ID when found")
    void shouldReturnCreditApplicationByIdWhenFound() {
        // Given
        when(creditApplicationRepositoryPort.findById(testCreditApplication.getId())).thenReturn(Optional.of(testCreditApplication));

        // When
        Optional<CreditApplication> foundApplication = getCreditApplicationService.getCreditApplicationById(testCreditApplication.getId());

        // Then
        assertTrue(foundApplication.isPresent());
        assertEquals(testCreditApplication.getId(), foundApplication.get().getId());
        verify(creditApplicationRepositoryPort, times(1)).findById(testCreditApplication.getId());
    }

    @Test
    @DisplayName("Should return empty when credit application by ID not found")
    void shouldReturnEmptyWhenCreditApplicationByIdNotFound() {
        // Given
        String nonExistentId = "non-existent-id";
        when(creditApplicationRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<CreditApplication> foundApplication = getCreditApplicationService.getCreditApplicationById(nonExistentId);

        // Then
        assertFalse(foundApplication.isPresent());
        verify(creditApplicationRepositoryPort, times(1)).findById(nonExistentId);
    }
}
