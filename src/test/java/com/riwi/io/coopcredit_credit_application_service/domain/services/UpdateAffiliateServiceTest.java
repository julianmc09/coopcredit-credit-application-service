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
                .document("100000000")
                .name("Original Name")
                .salary(new BigDecimal("2000.00"))
                .affiliationDate(LocalDate.now().minusYears(1))
                .status(AffiliateStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should update an existing affiliate successfully")
    void shouldUpdateExistingAffiliateSuccessfully() {
        // Given
        String updatedName = "Updated Name";
        BigDecimal updatedSalary = new BigDecimal("3000.00");
        LocalDate updatedAffiliationDate = LocalDate.now().minusMonths(6);
        AffiliateStatus updatedStatus = AffiliateStatus.INACTIVE;

        when(affiliateRepositoryPort.findById(existingAffiliate.getId())).thenReturn(Optional.of(existingAffiliate));
        when(affiliateRepositoryPort.save(any(Affiliate.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Optional<Affiliate> result = updateAffiliateService.updateAffiliate(
                existingAffiliate.getId(),
                updatedName,
                updatedSalary,
                updatedAffiliationDate,
                updatedStatus
        );

        // Then
        assertTrue(result.isPresent());
        Affiliate updatedAffiliate = result.get();
        assertEquals(existingAffiliate.getId(), updatedAffiliate.getId());
        assertEquals(updatedName, updatedAffiliate.getName());
        assertEquals(updatedSalary, updatedAffiliate.getSalary());
        assertEquals(updatedAffiliationDate, updatedAffiliate.getAffiliationDate());
        assertEquals(updatedStatus, updatedAffiliate.getStatus());

        verify(affiliateRepositoryPort, times(1)).findById(existingAffiliate.getId());
        verify(affiliateRepositoryPort, times(1)).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should return empty when affiliate to update is not found")
    void shouldReturnEmptyWhenAffiliateToUpdateNotFound() {
        // Given
        String nonExistentId = "non-existent-id";
        when(affiliateRepositoryPort.findById(nonExistentId)).thenReturn(Optional.empty());

        // When
        Optional<Affiliate> result = updateAffiliateService.updateAffiliate(
                nonExistentId,
                "Any Name",
                new BigDecimal("1000.00"),
                LocalDate.now(),
                AffiliateStatus.ACTIVE
        );

        // Then
        assertFalse(result.isPresent());
        verify(affiliateRepositoryPort, times(1)).findById(nonExistentId);
        verify(affiliateRepositoryPort, never()).save(any(Affiliate.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updated salary is zero or less")
    void shouldThrowExceptionWhenUpdatedSalaryIsZeroOrLess() {
        // Given
        BigDecimal invalidSalary = BigDecimal.ZERO;

        when(affiliateRepositoryPort.findById(existingAffiliate.getId())).thenReturn(Optional.of(existingAffiliate));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                updateAffiliateService.updateAffiliate(
                        existingAffiliate.getId(),
                        "Any Name",
                        invalidSalary,
                        LocalDate.now(),
                        AffiliateStatus.ACTIVE
                )
        );

        assertEquals("Salary must be greater than zero.", exception.getMessage());
        verify(affiliateRepositoryPort, times(1)).findById(existingAffiliate.getId());
        verify(affiliateRepositoryPort, never()).save(any(Affiliate.class));
    }
}
