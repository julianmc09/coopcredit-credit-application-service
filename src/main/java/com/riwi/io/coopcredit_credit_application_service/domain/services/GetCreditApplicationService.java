package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.GetCreditApplicationUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.configuration.security.SecurityHelper;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class GetCreditApplicationService implements GetCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepositoryPort;
    private final SecurityHelper securityHelper;

    @Override
    public Optional<CreditApplication> getCreditApplicationById(String id) {
        Optional<CreditApplication> creditApplication = creditApplicationRepositoryPort.findById(id);
        
        if (creditApplication.isEmpty()) {
            return Optional.empty();
        }

        // Check if current user is an AFILIADO trying to access someone else's application
        Optional<String> currentAffiliateId = securityHelper.getCurrentAffiliateId();
        if (currentAffiliateId.isPresent()) {
            String applicationAffiliateId = creditApplication.get().getAffiliate().getId();
            if (!currentAffiliateId.get().equals(applicationAffiliateId)) {
                throw new AccessDeniedException("You can only access your own credit applications.");
            }
        }

        return creditApplication;
    }
}
