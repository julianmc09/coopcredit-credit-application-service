package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories;

import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.AffiliateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AffiliateJpaRepository extends JpaRepository<AffiliateEntity, String> {
    Optional<AffiliateEntity> findByDocument(String document);
}
