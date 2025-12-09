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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterAffiliateServiceTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @InjectMocks
    private RegisterAffiliateService registerAffiliateService;

    private Affiliate testAffiliate;

    @BeforeEach
    void setUp() {
        testAffiliate = Affiliate.builder()
                .id("test-id")
                .document("123456789")
                .name("Test Affiliate")
                .salary(new BigDecimal("2000.00"))
                .affiliationDate(LocalDate.now())
                .status(AffiliateStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should register a new affiliate successfully")
    void shouldRegisterNewAffiliateSuccessfully() {
        // Given
        String document = "987654321";
        String name = "New Affiliate";
        BigDecimal salary = new BigDecimal("3000.00");
        LocalDate affiliationDate = LocalDate.now();

        when(affiliateRepositoryPort.findByDocument(document)).thenReturn(Optional.empty());
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenReturn(testAffiliate);

        // When
        Affiliate registeredAffiliate = registerAffiliateService.registerAffiliate(document, name, salary, affiliationDate);

        // Then
        assertNotNull(registeredAffiliate);
        assertEquals(testAffiliate.getDocument(), registeredAffiliate.getDocument());
        assertEquals(testAffiliate.getName(), registeredAffiliate.getName());
        assertEquals(testAffiliate.getSalary(), registeredAffiliate.getSalary());
        assertEquals(testAffiliate.getAffiliationDate(), registeredAffiliate.getAffiliationDate());
        assertEquals(AffiliateStatus.ACTIVE, registeredAffiliate.getStatus());

        verify(affiliateRepositoryPort, times(1)).findByDocument(document);
        verify(affiliateRepositoryPort, times(1)).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when salary is zero or less")
    void shouldThrowExceptionWhenSalaryIsZeroOrLess() {
        // Given
        String document = "987654321";
        String name = "New Affiliate";
        BigDecimal salary = BigDecimal.ZERO; // Invalid salary
        LocalDate affiliationDate = LocalDate.now();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerAffiliateService.registerAffiliate(document, name, salary, affiliationDate)
        );

        assertEquals("Salary must be greater than zero.", exception.getMessage());
        verify(affiliateRepositoryPort, never()).findByDocument(anyString());
        verify(affiliateRepositoryPort, never()).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when affiliate with document already exists")
    void shouldThrowExceptionWhenAffiliateDocumentAlreadyExists() {
        // Given
        String document = "123456789"; // Document already exists
        String name = "Existing Affiliate";
        BigDecimal salary = new BigDecimal("2500.00");
        LocalDate affiliationDate = LocalDate.now();

        when(affiliateRepositoryPort.findByDocument(document)).thenReturn(Optional.of(testAffiliate));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                registerAffiliateService.registerAffiliate(document, name, salary, affiliationDate)
        );

        assertEquals("Affiliate with document " + document + " already exists.", exception.getMessage());
        verify(affiliateRepositoryPort, times(1)).findByDocument(document);
        verify(affiliateRepositoryPort, never()).save(any(Affiliate.class));
    }
}
