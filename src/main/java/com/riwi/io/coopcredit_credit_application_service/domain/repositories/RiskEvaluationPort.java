package com.riwi.io.coopcredit_credit_application_service.domain.repositories;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.RiskEvaluation;

import java.math.BigDecimal;

public interface RiskEvaluationPort {

    RiskEvaluation evaluate(String document, BigDecimal amount, Integer term);
}
