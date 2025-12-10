package com.riwi.io.coopcredit_credit_application_service.domain.repositories;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;

import java.util.List;
import java.util.Optional;

public interface AffiliateRepositoryPort {

    Affiliate save(Affiliate affiliate);

    Optional<Affiliate> findById(String id);

    List<Affiliate> findAll();

    void deleteById(String id);

    Optional<Affiliate> findByDocument(String document);

    void deleteAll(); // Added this method
}
