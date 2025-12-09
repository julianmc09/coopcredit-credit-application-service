package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetPendingCreditApplicationsServiceTest {

    @Mock
    private CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @InjectMocks
    private GetPendingCreditApplicationsService getPendingCreditApplicationsService;

    private Affiliate testAffiliate;
    private CreditApplication pendingApplication1;
    private CreditApplication pendingApplication2;
    private CreditApplication approvedApplication;
    private CreditApplication rejectedApplication;

    @BeforeEach
    void setUp() {
        testAffiliate = Affiliate.builder()
                .id("affiliate-123")
                .document("100000000")
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();

        pendingApplication1 = CreditApplication.builder()
                .id("app-1")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();

        pendingApplication2 = CreditApplication.builder()
                .id("app-2")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("5000.00"))
                .term(12)
                .proposedRate(new BigDecimal("0.06"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();

        approvedApplication = CreditApplication.builder()
                .id("app-3")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("15000.00"))
                .term(36)
                .proposedRate(new BigDecimal("0.04"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.APPROVED)
                .build();

        rejectedApplication = CreditApplication.builder()
                .id("app-4")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("2000.00"))
                .term(6)
                .proposedRate(new BigDecimal("0.07"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.REJECTED)
                .build();
    }

    @Test
    @DisplayName("Should return only pending credit applications when available")
    void shouldReturnOnlyPendingCreditApplicationsWhenAvailable() {
        // Given
        List<CreditApplication> allApplications = Arrays.asList(pendingApplication1, pendingApplication2, approvedApplication, rejectedApplication);
        when(creditApplicationRepositoryPort.findAll()).thenReturn(allApplications);

        // When
        List<CreditApplication> result = getPendingCreditApplicationsService.getPendingCreditApplications();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(pendingApplication1));
        assertTrue(result.contains(pendingApplication2));
        assertFalse(result.contains(approvedApplication));
        assertFalse(result.contains(rejectedApplication));
        verify(creditApplicationRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no pending credit applications are available")
    void shouldReturnEmptyListWhenNoPendingCreditApplicationsAvailable() {
        // Given
        List<CreditApplication> nonPendingApplications = Arrays.asList(approvedApplication, rejectedApplication);
        when(creditApplicationRepositoryPort.findAll()).thenReturn(nonPendingApplications);

        // When
        List<CreditApplication> result = getPendingCreditApplicationsService.getPendingCreditApplications();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(creditApplicationRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no applications at all are available")
    void shouldReturnEmptyListWhenNoApplicationsAtAll() {
        // Given
        when(creditApplicationRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CreditApplication> result = getPendingCreditApplicationsService.getPendingCreditApplications();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(creditApplicationRepositoryPort, times(1)).findAll();
    }
}
