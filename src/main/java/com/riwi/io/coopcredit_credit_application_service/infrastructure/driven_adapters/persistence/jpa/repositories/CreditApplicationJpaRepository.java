package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.CreditApplicationEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditApplicationJpaRepository extends JpaRepository<CreditApplicationEntity, String> {

    @EntityGraph(attributePaths = {"affiliate", "riskEvaluation"})
    @Override
    Optional<CreditApplicationEntity> findById(String id);

    @EntityGraph(attributePaths = {"affiliate", "riskEvaluation"})
    @Override
    List<CreditApplicationEntity> findAll();

    @EntityGraph(attributePaths = {"affiliate", "riskEvaluation"})
    @Query("SELECT ca FROM CreditApplicationEntity ca WHERE ca.status = :status")
    List<CreditApplicationEntity> findByStatus(@Param("status") CreditApplicationStatus status);

    @EntityGraph(attributePaths = {"affiliate", "riskEvaluation"})
    @Query("SELECT ca FROM CreditApplicationEntity ca WHERE ca.affiliate.id = :affiliateId")
    List<CreditApplicationEntity> findByAffiliateId(@Param("affiliateId") String affiliateId);
}
