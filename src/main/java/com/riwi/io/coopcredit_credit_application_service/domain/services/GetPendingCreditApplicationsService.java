package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.GetPendingCreditApplicationsUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GetPendingCreditApplicationsService implements GetPendingCreditApplicationsUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @Override
    public List<CreditApplication> getPendingCreditApplications() {
        // Use optimized query with EntityGraph instead of filtering in memory
        return creditApplicationRepositoryPort.findByStatus(CreditApplicationStatus.PENDING);
    }
}