package com.riwi.io.coopcredit_credit_application_service.domain.repositories;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;

import java.util.List;
import java.util.Optional;

public interface CreditApplicationRepositoryPort {

    CreditApplication save(CreditApplication creditApplication);

    Optional<CreditApplication> findById(String id);

    List<CreditApplication> findAll();

    void deleteById(String id);
}
