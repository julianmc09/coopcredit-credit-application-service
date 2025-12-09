package com.riwi.io.coopcredit_credit_application_service.domain.ports.in;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;

import java.util.Optional;

public interface GetCreditApplicationUseCase {
    Optional<CreditApplication> getCreditApplicationById(String id);
}
