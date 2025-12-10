package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.AffiliateStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "affiliates")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AffiliateEntity {
    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String document;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal salary;

    @Column(nullable = false)
    private LocalDate affiliationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AffiliateStatus status;

    @OneToMany(mappedBy = "affiliate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @org.hibernate.annotations.BatchSize(size = 10) // Optimize batch loading of credit applications
    private List<CreditApplicationEntity> creditApplications;
}
