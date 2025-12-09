package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationResponse {
    private String id;
    private BigDecimal requestedAmount;
    private Integer term; // Plazo en meses
    private BigDecimal proposedRate;
    private LocalDate applicationDate;
    private CreditApplicationStatus status;
    private AffiliateResponse affiliate; // Use AffiliateResponse for nested object
    private RiskEvaluationResponse riskEvaluation; // Use RiskEvaluationResponse for nested object
}
