package com.riwi.io.coopcredit_credit_application_service.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder; // Reverted to @Builder
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder // Reverted to @Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplication {
    private String id;
    private BigDecimal requestedAmount;
    private Integer term; // Plazo en meses
    private BigDecimal proposedRate;
    private LocalDate applicationDate;
    private CreditApplicationStatus status;
    private Affiliate affiliate;
    private RiskEvaluation riskEvaluation;
}
