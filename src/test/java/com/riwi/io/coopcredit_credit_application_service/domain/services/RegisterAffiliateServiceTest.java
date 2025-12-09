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
import static org.mockito.Mockito.when;

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
                .id("some-uuid")
                .document("123456789")
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now())
                .status(AffiliateStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should register an affiliate successfully")
    void shouldRegisterAffiliateSuccessfully() {
        when(affiliateRepositoryPort.findByDocument(any(String.class))).thenReturn(Optional.empty());
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenReturn(testAffiliate);

        Affiliate registeredAffiliate = registerAffiliateService.registerAffiliate(
                testAffiliate.getDocument(),
                testAffiliate.getName(),
                testAffiliate.getSalary(),
                testAffiliate.getAffiliationDate()
        );

        assertNotNull(registeredAffiliate);
        assertEquals(testAffiliate.getDocument(), registeredAffiliate.getDocument());
        assertEquals(testAffiliate.getName(), registeredAffiliate.getName());
        assertEquals(testAffiliate.getSalary(), registeredAffiliate.getSalary());
        assertEquals(AffiliateStatus.ACTIVE, registeredAffiliate.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when salary is zero or less")
    void shouldThrowExceptionWhenSalaryIsZeroOrLess() {
        BigDecimal invalidSalary = BigDecimal.ZERO;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerAffiliateService.registerAffiliate(
                    "987654321",
                    "Jane Doe",
                    invalidSalary,
                    LocalDate.now()
            );
        });

        assertEquals("Salary must be greater than zero.", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when affiliate with document already exists")
    void shouldThrowExceptionWhenAffiliateDocumentAlreadyExists() {
        when(affiliateRepositoryPort.findByDocument(any(String.class))).thenReturn(Optional.of(testAffiliate));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            registerAffiliateService.registerAffiliate(
                    testAffiliate.getDocument(),
                    "Jane Doe",
                    new BigDecimal("6000.00"),
                    LocalDate.now()
            );
        });

        assertEquals("Affiliate with document " + testAffiliate.getDocument() + " already exists.", exception.getMessage());
    }
}
