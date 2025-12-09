package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiskEvaluationResponse {
    private String id;
    private Integer score;
    private String riskLevel;
    private String detail;
    private String reason;
}
