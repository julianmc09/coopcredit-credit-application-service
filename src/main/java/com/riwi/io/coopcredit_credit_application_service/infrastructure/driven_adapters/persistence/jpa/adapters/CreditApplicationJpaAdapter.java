package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.adapters;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers.CreditApplicationMapper;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories.CreditApplicationJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CreditApplicationJpaAdapter implements CreditApplicationRepositoryPort {

    private final CreditApplicationJpaRepository creditApplicationJpaRepository;
    private final CreditApplicationMapper creditApplicationMapper;

    @Override
    public CreditApplication save(CreditApplication creditApplication) {
        return creditApplicationMapper.toDomain(creditApplicationJpaRepository.save(creditApplicationMapper.toEntity(creditApplication)));
    }

    @Override
    public Optional<CreditApplication> findById(String id) {
        return creditApplicationJpaRepository.findById(id).map(creditApplicationMapper::toDomain);
    }

    @Override
    public List<CreditApplication> findAll() {
        return creditApplicationMapper.toDomainList(creditApplicationJpaRepository.findAll());
    }

    @Override
    public void deleteById(String id) {
        creditApplicationJpaRepository.deleteById(id);
    }
}
