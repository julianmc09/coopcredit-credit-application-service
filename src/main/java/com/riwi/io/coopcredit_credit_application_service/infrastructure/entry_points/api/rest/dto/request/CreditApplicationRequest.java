package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationRequest {
    @NotBlank(message = "Affiliate ID is required")
    private String affiliateId;
    @NotNull(message = "Requested amount is required")
    @DecimalMin(value = "0.01", message = "Requested amount must be greater than zero")
    private BigDecimal requestedAmount;
    @NotNull(message = "Term is required")
    @Min(value = 1, message = "Term must be at least 1 month")
    private Integer term; // Plazo en meses
    @NotNull(message = "Proposed rate is required")
    @DecimalMin(value = "0.01", message = "Proposed rate must be greater than zero")
    private BigDecimal proposedRate;
}
