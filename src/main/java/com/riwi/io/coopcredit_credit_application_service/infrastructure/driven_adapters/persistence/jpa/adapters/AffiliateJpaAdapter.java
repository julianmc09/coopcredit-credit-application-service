package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.adapters;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.AffiliateEntity;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers.AffiliateMapper;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories.AffiliateJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class AffiliateJpaAdapter implements AffiliateRepositoryPort {

    private final AffiliateJpaRepository affiliateJpaRepository;
    private final AffiliateMapper affiliateMapper;

    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = affiliateMapper.toEntity(affiliate);
        return affiliateMapper.toDomain(affiliateJpaRepository.save(entity));
    }

    @Override
    public Optional<Affiliate> findById(String id) {
        return affiliateJpaRepository.findById(id).map(affiliateMapper::toDomain);
    }

    @Override
    public List<Affiliate> findAll() {
        return affiliateMapper.toDomainList(affiliateJpaRepository.findAll());
    }

    @Override
    public void deleteById(String id) {
        affiliateJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Affiliate> findByDocument(String document) {
        return affiliateJpaRepository.findByDocument(document).map(affiliateMapper::toDomain);
    }

    @Override
    public void deleteAll() {
        affiliateJpaRepository.deleteAll();
    }
}
