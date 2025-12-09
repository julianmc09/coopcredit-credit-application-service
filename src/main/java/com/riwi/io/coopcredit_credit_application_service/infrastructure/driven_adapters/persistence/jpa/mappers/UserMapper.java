package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(source = "role", target = "role")
    User toDomain(UserEntity entity);

    @Mapping(source = "role", target = "role")
    UserEntity toEntity(User domain);

    List<User> toDomainList(List<UserEntity> entities);
    List<UserEntity> toEntityList(List<User> domains);
}
