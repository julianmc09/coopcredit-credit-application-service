package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
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
public class AffiliateResponse {
    private String id;
    private String document;
    private String name;
    private BigDecimal salary;
    private LocalDate affiliationDate;
    private AffiliateStatus status;
}
