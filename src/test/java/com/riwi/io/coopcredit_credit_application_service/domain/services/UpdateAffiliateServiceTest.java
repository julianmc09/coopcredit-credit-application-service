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
class UpdateAffiliateServiceTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @InjectMocks
    private UpdateAffiliateService updateAffiliateService;

    private Affiliate existingAffiliate;

    @BeforeEach
    void setUp() {
        existingAffiliate = Affiliate.builder()
                .id("affiliate-123")
                .document("123456789")
                .name("John Doe")
                .salary(new BigDecimal("5000.00"))
                .affiliationDate(LocalDate.now().minusMonths(12))
                .status(AffiliateStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should update an existing affiliate successfully")
    void shouldUpdateExistingAffiliateSuccessfully() {
        when(affiliateRepositoryPort.findById(any(String.class))).thenReturn(Optional.of(existingAffiliate));
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenReturn(existingAffiliate);

        String newName = "Johnathan Doe";
        BigDecimal newSalary = new BigDecimal("6000.00");
        AffiliateStatus newStatus = AffiliateStatus.INACTIVE;

        Optional<Affiliate> updatedAffiliate = updateAffiliateService.updateAffiliate(
                existingAffiliate.getId(),
                newName,
                newSalary,
                existingAffiliate.getAffiliationDate(),
                newStatus
        );

        assertTrue(updatedAffiliate.isPresent());
        assertEquals(newName, updatedAffiliate.get().getName());
        assertEquals(newSalary, updatedAffiliate.get().getSalary());
        assertEquals(newStatus, updatedAffiliate.get().getStatus());
    }

    @Test
    @DisplayName("Should return empty optional when affiliate to update is not found")
    void shouldReturnEmptyOptionalWhenAffiliateNotFound() {
        when(affiliateRepositoryPort.findById(any(String.class))).thenReturn(Optional.empty());

        Optional<Affiliate> updatedAffiliate = updateAffiliateService.updateAffiliate(
                "non-existent-id",
                "Jane Doe",
                new BigDecimal("7000.00"),
                LocalDate.now(),
                AffiliateStatus.ACTIVE
        );

        assertFalse(updatedAffiliate.isPresent());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updated salary is zero or less")
    void shouldThrowExceptionWhenUpdatedSalaryIsZeroOrLess() {
        when(affiliateRepositoryPort.findById(any(String.class))).thenReturn(Optional.of(existingAffiliate));
        BigDecimal invalidSalary = BigDecimal.ZERO;

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            updateAffiliateService.updateAffiliate(
                    existingAffiliate.getId(),
                    "Jane Doe",
                    invalidSalary,
                    LocalDate.now(),
                    AffiliateStatus.ACTIVE
            );
        });

        assertEquals("Salary must be greater than zero.", exception.getMessage());
    }
}
