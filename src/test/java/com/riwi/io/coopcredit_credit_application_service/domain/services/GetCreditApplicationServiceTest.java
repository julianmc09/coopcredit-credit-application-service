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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCreditApplicationServiceTest {

    @Mock
    private CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @InjectMocks
    private GetCreditApplicationService getCreditApplicationService; // For getCreditApplicationById

    @InjectMocks
    private GetAllCreditApplicationsService getAllCreditApplicationsService; // For getAllCreditApplications

    @InjectMocks
    private GetPendingCreditApplicationsService getPendingCreditApplicationsService; // For getPendingCreditApplications

    private CreditApplication pendingApp;
    private CreditApplication approvedApp;
    private CreditApplication rejectedApp;

    @BeforeEach
    void setUp() {
        Affiliate testAffiliate = Affiliate.builder()
                .id("affiliate-1")
                .document("111111111")
                .name("Alice")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();

        pendingApp = CreditApplication.builder()
                .id("app-pending")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("10000.00"))
                .term(24)
                .proposedRate(new BigDecimal("0.05"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();

        approvedApp = CreditApplication.builder()
                .id("app-approved")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("15000.00"))
                .term(36)
                .proposedRate(new BigDecimal("0.04"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.APPROVED)
                .build();

        rejectedApp = CreditApplication.builder()
                .id("app-rejected")
                .affiliate(testAffiliate)
                .requestedAmount(new BigDecimal("5000.00"))
                .term(12)
                .proposedRate(new BigDecimal("0.06"))
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.REJECTED)
                .build();
    }

    @Test
    @DisplayName("Should retrieve a credit application by ID successfully")
    void shouldGetCreditApplicationByIdSuccessfully() {
        when(creditApplicationRepositoryPort.findById(pendingApp.getId())).thenReturn(Optional.of(pendingApp));

        Optional<CreditApplication> foundApp = getCreditApplicationService.getCreditApplicationById(pendingApp.getId());

        assertTrue(foundApp.isPresent());
        assertEquals(pendingApp.getId(), foundApp.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional when credit application not found by ID")
    void shouldReturnEmptyOptionalWhenCreditApplicationByIdNotFound() {
        when(creditApplicationRepositoryPort.findById(any(String.class))).thenReturn(Optional.empty());

        Optional<CreditApplication> foundApp = getCreditApplicationService.getCreditApplicationById("non-existent-id");

        assertFalse(foundApp.isPresent());
    }

    @Test
    @DisplayName("Should retrieve all credit applications successfully")
    void shouldGetAllCreditApplicationsSuccessfully() {
        List<CreditApplication> allApps = Arrays.asList(pendingApp, approvedApp, rejectedApp);
        when(creditApplicationRepositoryPort.findAll()).thenReturn(allApps);

        List<CreditApplication> retrievedApps = getAllCreditApplicationsService.getAllCreditApplications();

        assertNotNull(retrievedApps);
        assertEquals(3, retrievedApps.size());
        assertTrue(retrievedApps.contains(pendingApp));
        assertTrue(retrievedApps.contains(approvedApp));
        assertTrue(retrievedApps.contains(rejectedApp));
    }

    @Test
    @DisplayName("Should retrieve only pending credit applications successfully")
    void shouldGetPendingCreditApplicationsSuccessfully() {
        List<CreditApplication> allApps = Arrays.asList(pendingApp, approvedApp, rejectedApp);
        when(creditApplicationRepositoryPort.findAll()).thenReturn(allApps);

        List<CreditApplication> retrievedPendingApps = getPendingCreditApplicationsService.getPendingCreditApplications();

        assertNotNull(retrievedPendingApps);
        assertEquals(1, retrievedPendingApps.size());
        assertTrue(retrievedPendingApps.contains(pendingApp));
        assertFalse(retrievedPendingApps.contains(approvedApp));
        assertFalse(retrievedPendingApps.contains(rejectedApp));
    }
}
