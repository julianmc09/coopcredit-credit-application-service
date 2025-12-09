package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.GetAffiliateUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GetAffiliateService implements GetAffiliateUseCase {

    private final AffiliateRepositoryPort affiliateRepositoryPort;

    @Override
    public Optional<Affiliate> getAffiliateById(String id) {
        return affiliateRepositoryPort.findById(id);
    }

    @Override
    public Optional<Affiliate> getAffiliateByDocument(String document) {
        return affiliateRepositoryPort.findByDocument(document);
    }

    @Override
    public List<Affiliate> getAllAffiliates() {
        return affiliateRepositoryPort.findAll();
    }
}
