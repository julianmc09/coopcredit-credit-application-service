package com.riwi.io.coopcredit_credit_application_service.domain.ports.in;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;

public interface EvaluateCreditApplicationUseCase {
    CreditApplication evaluateCreditApplication(String creditApplicationId);
}
