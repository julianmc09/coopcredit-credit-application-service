package com.riwi.io.coopcredit_credit_application_service.domain.ports.in;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;

import java.math.BigDecimal;

public interface RegisterCreditApplicationUseCase {
    CreditApplication registerCreditApplication(String affiliateId, BigDecimal requestedAmount, Integer term, BigDecimal proposedRate);
}
