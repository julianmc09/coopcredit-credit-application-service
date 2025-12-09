package com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.http_client.risk_central;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.RiskEvaluation;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.RiskEvaluationPort;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.http_client.risk_central.dtos.RiskRequest;
import com.riwi.io.coopcredit_credit_application_service.infrastructure.driven_adapters.http_client.risk_central.dtos.RiskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RiskCentralHttpClientAdapter implements RiskEvaluationPort {

    private final RestTemplate restTemplate;

    @Value("${risk-central.base-url}")
    private String riskCentralBaseUrl;

    @Override
    public RiskEvaluation evaluate(String document, BigDecimal amount, Integer term) {
        String url = riskCentralBaseUrl + "/risk-evaluation"; // Endpoint for risk evaluation

        RiskRequest request = RiskRequest.builder()
                .document(document)
                .amount(amount)
                .term(term)
                .build();

        RiskResponse response = restTemplate.postForObject(url, request, RiskResponse.class);

        if (response == null) {
            throw new RuntimeException("Received null response from risk central service.");
        }

        // Map RiskResponse to domain RiskEvaluation entity
        return RiskEvaluation.builder()
                .id(UUID.randomUUID().toString()) // Generate a new ID for our internal domain entity
                .score(response.getScore())
                .riskLevel(response.getRiskLevel())
                .detail(response.getDetail())
                .reason("Evaluated by external risk central.") // Default reason for external evaluation
                .build();
    }
}
