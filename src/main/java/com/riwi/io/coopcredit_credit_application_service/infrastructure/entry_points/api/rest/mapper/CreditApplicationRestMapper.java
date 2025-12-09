package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.mapper;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.request.CreditApplicationRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response.CreditApplicationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AffiliateRestMapper.class, RiskEvaluationRestMapper.class})
public interface CreditApplicationRestMapper {
    @Mapping(target = "affiliate", ignore = true) // Affiliate will be fetched separately by service
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "riskEvaluation", ignore = true)
    CreditApplication toDomain(CreditApplicationRequest request);

    @Mapping(source = "affiliate", target = "affiliate")
    @Mapping(source = "riskEvaluation", target = "riskEvaluation")
    CreditApplicationResponse toResponse(CreditApplication domain);

    List<CreditApplicationResponse> toResponseList(List<CreditApplication> domains);
}
