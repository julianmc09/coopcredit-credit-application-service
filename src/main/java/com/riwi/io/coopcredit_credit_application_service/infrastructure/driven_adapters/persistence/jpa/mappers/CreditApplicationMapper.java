package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.CreditApplicationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {AffiliateMapper.class, RiskEvaluationMapper.class})
public interface CreditApplicationMapper {
    @Mapping(source = "affiliate", target = "affiliate")
    @Mapping(source = "riskEvaluation", target = "riskEvaluation")
    CreditApplication toDomain(CreditApplicationEntity entity);

    @Mapping(source = "affiliate", target = "affiliate")
    @Mapping(source = "riskEvaluation", target = "riskEvaluation")
    CreditApplicationEntity toEntity(CreditApplication domain);

    List<CreditApplication> toDomainList(List<CreditApplicationEntity> entities);
    List<CreditApplicationEntity> toEntityList(List<CreditApplication> domains);
}
