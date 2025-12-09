package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.repositories;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(Role name);
}
