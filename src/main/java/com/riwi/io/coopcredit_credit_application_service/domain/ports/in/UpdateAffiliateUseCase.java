package com.riwi.io.coopcredit_credit_application_service.domain.ports.in;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

public interface UpdateAffiliateUseCase {
    Optional<Affiliate> updateAffiliate(String id, String name, BigDecimal salary, LocalDate affiliationDate, AffiliateStatus status);
}
