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
class GetAllCreditApplicationsServiceTest {

    @Mock
    private CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @InjectMocks
    private GetAllCreditApplicationsService getAllCreditApplicationsService;

    private Affiliate testAffiliate;
    private CreditApplication creditApplication1;
    private CreditApplication creditApplication2;

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

        creditApplication1 = CreditApplication.builder()
                .id("app-1")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.APPROVED)
                .build();

        creditApplication2 = CreditApplication.builder()
                .id("app-2")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("5000.00"))
                .term(12)
                .proposedRate(new BigDecimal("0.06"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();
    }

    @Test
    @DisplayName("Should return all credit applications when available")
    void shouldReturnAllCreditApplicationsWhenAvailable() {
        // Given
        List<CreditApplication> allApplications = Arrays.asList(creditApplication1, creditApplication2);
        when(creditApplicationRepositoryPort.findAll()).thenReturn(allApplications);

        // When
        List<CreditApplication> result = getAllCreditApplicationsService.getAllCreditApplications();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(creditApplication1));
        assertTrue(result.contains(creditApplication2));
        verify(creditApplicationRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no credit applications are available")
    void shouldReturnEmptyListWhenNoCreditApplicationsAvailable() {
        // Given
        when(creditApplicationRepositoryPort.findAll()).thenReturn(Collections.emptyList());

        // When
        List<CreditApplication> result = getAllCreditApplicationsService.getAllCreditApplications();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(creditApplicationRepositoryPort, times(1)).findAll();
    }
}
