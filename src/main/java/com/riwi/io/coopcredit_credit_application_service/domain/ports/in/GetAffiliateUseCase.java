package com.riwi.io.coopcredit_credit_application_service.domain.ports.in;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;

import java.util.List;
import java.util.Optional;

public interface GetAffiliateUseCase {
    Optional<Affiliate> getAffiliateById(String id);
    Optional<Affiliate> getAffiliateByDocument(String document);
    List<Affiliate> getAllAffiliates();
}
