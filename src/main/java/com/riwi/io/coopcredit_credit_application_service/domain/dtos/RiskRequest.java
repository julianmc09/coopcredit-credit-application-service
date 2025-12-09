package com.riwi.io.coopcredit_credit_application_service.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiskRequest {
    private String document;
    private BigDecimal amount;
    private Integer term;
}
