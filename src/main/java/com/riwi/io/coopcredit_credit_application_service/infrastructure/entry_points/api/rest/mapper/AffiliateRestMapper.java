package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.mapper;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.AffiliateRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response.AffiliateResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AffiliateRestMapper {
    Affiliate toDomain(AffiliateRequest request);
    AffiliateResponse toResponse(Affiliate domain);
    List<AffiliateResponse> toResponseList(List<Affiliate> domains);
}
