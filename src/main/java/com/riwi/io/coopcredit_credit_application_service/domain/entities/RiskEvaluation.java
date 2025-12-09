package com.riwi.io.coopcredit_credit_application_service.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiskEvaluation {
    private String id;
    private Integer score;
    private String riskLevel;
    private String detail;
    private String reason; // Motivo de la decisión (APROBADO/RECHAZADO)
}
