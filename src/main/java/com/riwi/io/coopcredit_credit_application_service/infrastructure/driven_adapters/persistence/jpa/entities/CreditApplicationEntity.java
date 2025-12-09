package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "credit_applications")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditApplicationEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private BigDecimal requestedAmount;

    @Column(nullable = false)
    private Integer term; // Plazo en meses

    @Column(nullable = false)
    private BigDecimal proposedRate;

    @Column(nullable = false)
    private LocalDate applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CreditApplicationStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affiliate_id", nullable = false)
    private AffiliateEntity affiliate;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "risk_evaluation_id")
    private RiskEvaluationEntity riskEvaluation;
}
