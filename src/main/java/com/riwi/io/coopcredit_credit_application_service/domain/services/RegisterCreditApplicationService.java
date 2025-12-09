package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.RegisterCreditApplicationUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RegisterCreditApplicationService implements RegisterCreditApplicationUseCase {

    private final AffiliateRepositoryPort affiliateRepositoryPort;
    private final CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @Override
    public CreditApplication registerCreditApplication(String affiliateId, BigDecimal requestedAmount, Integer term, BigDecimal proposedRate) {
        // 1. Find the affiliate
        Affiliate affiliate = affiliateRepositoryPort.findById(affiliateId)
                .orElseThrow(() -> new IllegalArgumentException("Affiliate not found with ID: " + affiliateId));

        // 2. Validate affiliate status
        if (affiliate.getStatus() != AffiliateStatus.ACTIVE) {
            throw new IllegalArgumentException("Affiliate is not active and cannot apply for a credit.");
        }

        // 3. Basic validations for the application (more complex validations will be in EvaluateCreditApplication)
        if (requestedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Requested amount must be greater than zero.");
        }
        if (term <= 0) {
            throw new IllegalArgumentException("Term must be greater than zero.");
        }
        if (proposedRate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Proposed rate must be greater than zero.");
        }

        // 4. Create the credit application
        CreditApplication newApplication = CreditApplication.builder()
                .id(UUID.randomUUID().toString())
                .affiliate(affiliate)
                .requestedAmount(requestedAmount)
                .term(term)
                .proposedRate(proposedRate)
                .applicationDate(LocalDate.now())
                .status(CreditApplicationStatus.PENDING)
                .build();

        // 5. Save and return
        return creditApplicationRepositoryPort.save(newApplication);
    }
}
