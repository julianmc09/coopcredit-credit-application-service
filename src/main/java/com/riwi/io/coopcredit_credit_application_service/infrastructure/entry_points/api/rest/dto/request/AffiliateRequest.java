package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AffiliateRequest {
    @NotBlank(message = "Document is required")
    private String document;
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Salary is required")
    @DecimalMin(value = "0.01", message = "Salary must be greater than zero")
    private BigDecimal salary;
    @NotNull(message = "Affiliation date is required")
    private LocalDate affiliationDate;
    @NotNull(message = "Status is required")
    private AffiliateStatus status;
}
