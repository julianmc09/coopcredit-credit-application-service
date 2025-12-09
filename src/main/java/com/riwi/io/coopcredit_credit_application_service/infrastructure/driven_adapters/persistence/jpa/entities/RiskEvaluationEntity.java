package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.persistence.jpa.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "risk_evaluations")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RiskEvaluationEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private String riskLevel;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String detail;

    @Column(columnDefinition = "TEXT")
    private String reason; // Motivo de la decisión (APROBADO/RECHAZADO)
}
