package com.riwi.io.coopcredit_credit_application_service.domain.services;

import com.riwi.io.coopcredit_credit_application_service.domain.entities.Affiliate;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplication;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.CreditApplicationStatus;
import com.riwi.io.coopcredit_credit_application_service.domain.entities.RiskEvaluation;
import com.riwi.io.coopcredit_credit_application_service.domain.ports.in.EvaluateCreditApplicationUseCase;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.AffiliateRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.CreditApplicationRepositoryPort;
import com.riwi.io.coopcredit_credit_application_service.domain.repositories.RiskEvaluationPort;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EvaluateCreditApplicationService implements EvaluateCreditApplicationUseCase {

    private final CreditApplicationRepositoryPort creditApplicationRepositoryPort;
    private final AffiliateRepositoryPort affiliateRepositoryPort;
    private final RiskEvaluationPort riskEvaluationPort;

    // Policy constants
    private static final BigDecimal MAX_QUOTA_INCOME_RATIO = new BigDecimal("0.30"); // 30%
    private static final BigDecimal MAX_AMOUNT_SALARY_MULTIPLIER = new BigDecimal("10");
    private static final long MIN_AFFILIATION_MONTHS = 6;

    @Override
    @Transactional // Ensure the entire process is transactional
    public CreditApplication evaluateCreditApplication(String creditApplicationId) {
        // 1. Find the credit application
        CreditApplication creditApplication = creditApplicationRepositoryPort.findById(creditApplicationId)
                .orElseThrow(() -> new IllegalArgumentException("Credit Application not found with ID: " + creditApplicationId));

        // 2. Check if the application is pending
        if (creditApplication.getStatus() != CreditApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Credit Application with ID: " + creditApplicationId + " is not in PENDING status.");
        }

        // 3. Get the associated affiliate
        Affiliate affiliate = affiliateRepositoryPort.findById(creditApplication.getAffiliate().getId())
                .orElseThrow(() -> new IllegalStateException("Affiliate not found for credit application ID: " + creditApplicationId));

        // 4. Invoke the external risk evaluation service
        // The RiskEvaluationPort is expected to return our domain RiskEvaluation entity
        RiskEvaluation externalRiskResult = riskEvaluationPort.evaluate(
                affiliate.getDocument(),
                creditApplication.getRequestedAmount(),
                creditApplication.getTerm()
        );

        // Prepare for decision making
        boolean isApproved = true;
        List<String> rejectionReasons = new ArrayList<>();

        // 5. Apply internal policies

        // Policy 1: Quota/Income Ratio (Cuota/Ingreso)
        // Ensure term is not zero to avoid division by zero
        if (creditApplication.getTerm() == null || creditApplication.getTerm() <= 0) {
            throw new IllegalArgumentException("Credit application term must be a positive integer.");
        }
        BigDecimal monthlyPayment = creditApplication.getRequestedAmount().divide(new BigDecimal(creditApplication.getTerm()), 2, RoundingMode.HALF_UP);
        BigDecimal maxAllowedMonthlyPayment = affiliate.getSalary().multiply(MAX_QUOTA_INCOME_RATIO);

        if (monthlyPayment.compareTo(maxAllowedMonthlyPayment) > 0) {
            isApproved = false;
            rejectionReasons.add("Monthly payment (" + monthlyPayment + ") exceeds " + (MAX_QUOTA_INCOME_RATIO.multiply(new BigDecimal("100"))) + "% of salary (" + maxAllowedMonthlyPayment + ").");
        }

        // Policy 2: Maximum Amount based on Salary (Monto máximo según salario)
        BigDecimal maxAllowedAmount = affiliate.getSalary().multiply(MAX_AMOUNT_SALARY_MULTIPLIER);
        if (creditApplication.getRequestedAmount().compareTo(maxAllowedAmount) > 0) {
            isApproved = false;
            rejectionReasons.add("Requested amount (" + creditApplication.getRequestedAmount() + ") exceeds " + MAX_AMOUNT_SALARY_MULTIPLIER + " times salary (" + maxAllowedAmount + ").");
        }

        // Policy 3: Minimum Affiliation Time (Antigüedad mínima)
        if (affiliate.getAffiliationDate() == null) {
            throw new IllegalStateException("Affiliation date is missing for affiliate ID: " + affiliate.getId());
        }
        long monthsSinceAffiliation = ChronoUnit.MONTHS.between(affiliate.getAffiliationDate(), LocalDate.now());
        if (monthsSinceAffiliation < MIN_AFFILIATION_MONTHS) {
            isApproved = false;
            rejectionReasons.add("Affiliate has not met the minimum " + MIN_AFFILIATION_MONTHS + " months of affiliation (currently " + monthsSinceAffiliation + " months).");
        }

        // Policy 4: Risk Level from external service
        if ("ALTO RIESGO".equalsIgnoreCase(externalRiskResult.getRiskLevel())) {
            isApproved = false;
            rejectionReasons.add("High risk level detected by external risk central: " + externalRiskResult.getRiskLevel() + " (Score: " + externalRiskResult.getScore() + ").");
        }

        // 6. Generate RiskEvaluation entity with combined reasons
        String finalReason = String.join("; ", rejectionReasons);
        if (finalReason.isEmpty()) { // If no specific rejection reasons, it means it passed all internal policies
            finalReason = "Credit application approved based on policies and risk evaluation.";
        } else {
            // If there are rejection reasons, the application is rejected
            isApproved = false; // Ensure this is set to false if reasons exist
        }

        RiskEvaluation finalRiskEvaluation = RiskEvaluation.builder()
                .id(UUID.randomUUID().toString()) // Generate a new ID for our internal RiskEvaluation record
                .score(externalRiskResult.getScore())
                .riskLevel(externalRiskResult.getRiskLevel())
                .detail(externalRiskResult.getDetail())
                .reason(finalReason)
                .build();

        // 7. Update CreditApplication status and associate RiskEvaluation
        creditApplication.setStatus(isApproved ? CreditApplicationStatus.APPROVED : CreditApplicationStatus.REJECTED);
        creditApplication.setRiskEvaluation(finalRiskEvaluation); // Use the new entity

        // 8. Save the updated credit application
        return creditApplicationRepositoryPort.save(creditApplication);
    }
}