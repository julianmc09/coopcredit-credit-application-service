package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.mappers;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities.AffiliateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AffiliateMapper {
    Affiliate toDomain(AffiliateEntity entity);
    AffiliateEntity toEntity(Affiliate domain);
    List<Affiliate> toDomainList(List<AffiliateEntity> entities);
    List<AffiliateEntity> toEntityList(List<Affiliate> domains);
}
