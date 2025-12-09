package com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.mapper;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.RiskEvaluation;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.entry_points.api.rest.dto.response.RiskEvaluationResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RiskEvaluationRestMapper {
    RiskEvaluationResponse toResponse(RiskEvaluation domain);
    List<RiskEvaluationResponse> toResponseList(List<RiskEvaluation> domains);
}
