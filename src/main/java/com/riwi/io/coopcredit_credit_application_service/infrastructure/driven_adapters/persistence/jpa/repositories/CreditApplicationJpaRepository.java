package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories;

import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, String> {
}
