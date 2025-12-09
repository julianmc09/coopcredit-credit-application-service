package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.GetAllCreditApplicationsUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class GetAllCreditApplicationsService implements GetAllCreditApplicationsUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepositoryPort;

    @Override
    public List<CreditApplication> getAllCreditApplications() {
        return creditApplicationRepositoryPort.findAll();
    }
}
