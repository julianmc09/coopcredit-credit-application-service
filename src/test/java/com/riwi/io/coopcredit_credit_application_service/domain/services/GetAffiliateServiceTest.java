package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAffiliateServiceTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @InjectMocks
    private GetAffiliateService getAffiliateService;

    private Affiliate testAffiliate1;
    private Affiliate testAffiliate2;

    @BeforeEach
    void setUp() {
        testAffiliate1 = Affiliate.builder()
                .id("affiliate-1")
                .document("111111111")
                .name("Affiliate One")
                .salary(new BigDecimal("1500.00"))
                .affiliationDate(LocalDate.now().minusMonths(10))
                .status(AffiliateStatus.ACTIVE)
                .build();

        testAffiliate2 = Affiliate.builder()
                .id("affiliate-2")
                .document("222222222")
                .name("Affiliate Two")
                .salary(new BigDecimal("2500.00"))
                .affiliationDate(LocalDate.now().minusMonths(5))
                .status(AffiliateStatus.INACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should return affiliate by ID when found")
    void shouldReturnAffiliateByIdWhenFound() {
        // Given
        when(affiliateRepositoryPort.findById(testAffiliate1.getId())).thenReturn(Optional.of(testAffiliate1));

        // When
        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateById(testAffiliate1.getId());

        // Then
        assertTrue(foundAffiliate.isPresent());
        assertEquals(testAffiliate1.getId(), foundAffiliate.get().getId());
        verify(affiliateRepositoryPort, times(1)).findById(testAffiliate1.getId());
    }

    @Test
    @DisplayName("Should return empty when affiliate by ID not found")
    void shouldReturnEmptyWhenAffiliateByIdNotFound() {
        // Given
        String nonExistentId = "non-existent-id";
        when(affiliateRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateById(nonExistentId);

        // Then
        assertFalse(foundAffiliate.isPresent());
        verify(affiliateRepositoryPort, times(1)).findById(nonExistentId);
    }

    @Test
    @DisplayName("Should return affiliate by document when found")
    void shouldReturnAffiliateByDocumentWhenFound() {
        // Given
        when(affiliateRepositoryPort.findByDocument(testAffiliate1.getDocument())).thenReturn(Optional.of(testAffiliate1));

        // When
        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateByDocument(testAffiliate1.getDocument());

        // Then
        assertTrue(foundAffiliate.isPresent());
        assertEquals(testAffiliate1.getDocument(), foundAffiliate.get().getDocument());
        verify(affiliateRepositoryPort, times(1)).findByDocument(testAffiliate1.getDocument());
    }

    @Test
    @DisplayName("Should return empty when affiliate by document not found")
    void shouldReturnEmptyWhenAffiliateByDocumentNotFound() {
        // Given
        String nonExistentDocument = "999999999";
        when(affiliateRepositoryPort.findByDocument(nonExistentDocument)).thenReturn(Optional.empty());

        // When
        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateByDocument(nonExistentDocument);

        // Then
        assertFalse(foundAffiliate.isPresent());
        verify(affiliateRepositoryPort, times(1)).findByDocument(nonExistentDocument);
    }

    @Test
    @DisplayName("Should return all affiliates when available")
    void shouldReturnAllAffiliatesWhenAvailable() {
        // Given
        List<Affiliate> allAffiliates = Arrays.asList(testAffiliate1, testAffiliate2);
        when(affiliateRepositoryPort.findAll()).thenReturn(allAffiliates);

        // When
        List<Affiliate> result = getAffiliateService.getAllAffiliates();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(testAffiliate1));
        assertTrue(result.contains(testAffiliate2));
        verify(affiliateRepositoryPort, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no affiliates are available")
    void shouldReturnEmptyListWhenNoAffiliatesAvailable() {
        // Given
        when(affiliateRepositoryPort.findAll()).thenReturn(List.of()); // Return an empty list

        // When
        List<Affiliate> result = getAffiliateService.getAllAffiliates();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(affiliateRepositoryPort, times(1)).findAll();
    }
}
