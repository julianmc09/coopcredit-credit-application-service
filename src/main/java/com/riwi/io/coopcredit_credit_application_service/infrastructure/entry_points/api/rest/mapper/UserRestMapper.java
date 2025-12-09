package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.mapper;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.User;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.RegisterUserRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserRestMapper {
    User toDomain(RegisterUserRequest request);
}
