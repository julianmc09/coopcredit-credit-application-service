package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.RegisterAffiliateUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegisterAffiliateService implements RegisterAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepositoryPort;

    @Override
    public Affiliate registerAffiliate(String document, String name, BigDecimal salary, LocalDate affiliationDate) {
        // Validations
        if (salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Salary must be greater than zero.");
        }
        if (affiliateRepositoryPort.findByDocument(document).isPresent()) {
            throw new IllegalArgumentException("Affiliate with document " + document + " already exists.");
        }

        Affiliate newAffiliate = Affiliate.builder()
                .id(UUID.randomUUID().toString())
                .document(document)
                .name(name)
                .salary(salary)
                .affiliationDate(affiliationDate)
                .status(AffiliateStatus.ACTIVE) // New affiliates are active by default
                .build();

        return affiliateRepositoryPort.save(newAffiliate);
    }
}
