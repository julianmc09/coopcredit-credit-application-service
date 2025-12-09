package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.UpdateAffiliateUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UpdateAffiliateService implements UpdateAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepositoryPort;

    @Override
    public Optional<Affiliate> updateAffiliate(String id, String name, BigDecimal salary, LocalDate affiliationDate, AffiliateStatus status) {
        // Find the affiliate by ID
        Optional<Affiliate> existingAffiliate = affiliateRepositoryPort.findById(id);

        if (existingAffiliate.isEmpty()) {
            return Optional.empty(); // Affiliate not found
        }

        // Apply validations
        if (salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than zero.");
        }

        // Update fields
        Affiliate affiliateToUpdate = existingAffiliate.get();
        affiliateToUpdate.setName(name);
        affiliateToUpdate.setSalary(salary);
        affiliateToUpdate.setAffiliationDate(affiliationDate);
        affiliateToUpdate.setStatus(status);

        return Optional.of(affiliateRepositoryPort.save(affiliateToUpdate));
    }
}
