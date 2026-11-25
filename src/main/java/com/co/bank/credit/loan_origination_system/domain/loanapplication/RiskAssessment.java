package com.co.bank.credit.loan_origination_system.domain.loanapplication;

import com.co.bank.credit.loan_origination_system.domain.shared.Money;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Resultado de la evaluación automática: score, cuota, ingreso, proporción cuota/ingreso
 * y la recomendación con sus motivos.
 */
public record RiskAssessment(
        CreditScore score,
        Money monthlyInstallment,
        Money monthlyIncome,
        BigDecimal paymentCapacityRatio,
        Recommendation recommendation,
        List<RecommendationReason> reasons,
        Instant assessedAt) {

    public RiskAssessment {
        requireNonNull(score, "Credit score cannot be null");
        requireNonNull(monthlyInstallment, "Monthly installment cannot be null");
        requireNonNull(monthlyIncome, "Monthly income cannot be null");
        requireNonNull(paymentCapacityRatio, "Payment capacity ratio cannot be null");
        requireNonNull(recommendation, "Recommendation cannot be null");
        requireNonNull(assessedAt, "Assessment date cannot be null");
        reasons = List.copyOf(requireNonNull(reasons, "Reasons cannot be null"));
    }

    public boolean recommendsApproval() {
        return recommendation == Recommendation.APPROVE;
    }
}
