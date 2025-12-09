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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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
                .name("Alice")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();

        testAffiliate2 = Affiliate.builder()
                .id("affiliate-2")
                .document("222222222")
                .name("Bob")
                .salary(new BigDecimal("6000.00"))
                .affiliationDate(LocalDate.now().minusMonths(6))
                .status(AffiliateStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should retrieve an affiliate by ID successfully")
    void shouldGetAffiliateByIdSuccessfully() {
        when(affiliateRepositoryPort.findById(testAffiliate1.getId())).thenReturn(Optional.of(testAffiliate1));

        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateById(testAffiliate1.getId());

        assertTrue(foundAffiliate.isPresent());
        assertEquals(testAffiliate1.getId(), foundAffiliate.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional when affiliate not found by ID")
    void shouldReturnEmptyOptionalWhenAffiliateByIdNotFound() {
        when(affiliateRepositoryPort.findById(any(String.class))).thenReturn(Optional.empty());

        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateById("non-existent-id");

        assertFalse(foundAffiliate.isPresent());
    }

    @Test
    @DisplayName("Should retrieve an affiliate by document successfully")
    void shouldGetAffiliateByDocumentSuccessfully() {
        when(affiliateRepositoryPort.findByDocument(testAffiliate1.getDocument())).thenReturn(Optional.of(testAffiliate1));

        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateByDocument(testAffiliate1.getDocument());

        assertTrue(foundAffiliate.isPresent());
        assertEquals(testAffiliate1.getDocument(), foundAffiliate.get().getDocument());
    }

    @Test
    @DisplayName("Should return empty optional when affiliate not found by document")
    void shouldReturnEmptyOptionalWhenAffiliateByDocumentNotFound() {
        when(affiliateRepositoryPort.findByDocument(any(String.class))).thenReturn(Optional.empty());

        Optional<Affiliate> foundAffiliate = getAffiliateService.getAffiliateByDocument("non-existent-document");

        assertFalse(foundAffiliate.isPresent());
    }

    @Test
    @DisplayName("Should retrieve all affiliates successfully")
    void shouldGetAllAffiliatesSuccessfully() {
        List<Affiliate> allAffiliates = Arrays.asList(testAffiliate1, testAffiliate2);
        when(affiliateRepositoryPort.findAll()).thenReturn(allAffiliates);

        List<Affiliate> retrievedAffiliates = getAffiliateService.getAllAffiliates();

        assertNotNull(retrievedAffiliates);
        assertEquals(2, retrievedAffiliates.size());
        assertTrue(retrievedAffiliates.contains(testAffiliate1));
        assertTrue(retrievedAffiliates.contains(testAffiliate2));
    }
}
