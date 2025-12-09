package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.RiskEvaluation;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.RiskEvaluationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RiskEvaluationMapper {
    RiskEvaluation toDomain(RiskEvaluationEntity entity);
    RiskEvaluationEntity toEntity(RiskEvaluation domain);
    List<RiskEvaluation> toDomainList(List<RiskEvaluationEntity> entities);
    List<RiskEvaluationEntity> toEntityList(List<RiskEvaluation> domains);
}
