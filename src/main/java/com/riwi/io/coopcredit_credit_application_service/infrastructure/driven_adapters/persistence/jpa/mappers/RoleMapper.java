package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Role;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    Role toDomain(RoleEntity entity);
    RoleEntity toEntity(Role domain);
    List<Role> toDomainList(List<RoleEntity> entities);
    List<RoleEntity> toEntityList(List<Role> domains);
}
