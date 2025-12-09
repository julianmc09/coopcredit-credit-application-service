package com.riwi.io.coopcredit_credit_application_service.domain.ports.in;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RegisterAffiliateUseCase {
    Affiliate registerAffiliate(String document, String name, BigDecimal salary, LocalDate affiliationDate);
}
