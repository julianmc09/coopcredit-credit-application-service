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
public class Affiliate {
    private String id;
    private String document;
    private String name;
    private BigDecimal salary;
    private LocalDate affiliationDate;
    private AffiliateStatus status;
}
