package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.http_client.risk_central.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiskResponse {
    private String document;
    private Integer score;
    private String riskLevel;
    private String detail;
}
