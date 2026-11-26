package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requirePositive;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireRange;

/**
 * Parámetros configurables de la política de crédito y las reglas que dependen de ellos:
 * RN-05 (capacidad de pago), RN-06 (recomendación), RN-07 (comité) y RN-08 (plazo de subsanación).
 */
public record CreditPolicy(
        int minimumScore,
        BigDecimal maxPaymentCapacityRatio,
        Money committeeThreshold,
        int remediationDays) {

    private static final int RATIO_SCALE = 4;

    public CreditPolicy {
        requireRange(minimumScore, CreditScore.MIN, CreditScore.MAX, "Minimum score");
        requirePositive(maxPaymentCapacityRatio, "Max payment capacity ratio");
        if (maxPaymentCapacityRatio.compareTo(BigDecimal.ONE) > 0) {
            throw new ValueObjectException("Max payment capacity ratio cannot exceed 1");
        }
        requireNonNull(committeeThreshold, "Committee threshold cannot be null");
        requireRange(remediationDays, 1, 90, "Remediation days");
    }

    /**
     * Valores aprobados el 2026-11-24: score 500, 40%, comité desde 50.000.000 COP, 5 días.
     */
    public static CreditPolicy standard() {
        return new CreditPolicy(500, new BigDecimal("0.40"), Money.of("50000000"), 5);
    }

    /**
     * RN-05 y RN-06: recomienda aprobar solo si el score alcanza el mínimo
     * y la cuota no supera la proporción máxima del ingreso.
     */
    public RiskAssessment assess(CreditScore score, Money monthlyInstallment, Money monthlyIncome, Instant now) {
        requireNonNull(score, "Credit score cannot be null");
        requireNonNull(monthlyInstallment, "Monthly installment cannot be null");
        requireNonNull(monthlyIncome, "Monthly income cannot be null");
        if (monthlyIncome.isZero()) {
            throw new ValueObjectException("Monthly income must be greater than zero");
        }

        BigDecimal ratio = monthlyInstallment.getAmount()
                .divide(monthlyIncome.getAmount(), RATIO_SCALE, RoundingMode.HALF_EVEN);

        List<RecommendationReason> reasons = new ArrayList<>();
        if (!score.isAtLeast(minimumScore)) {
            reasons.add(RecommendationReason.INSUFFICIENT_SCORE);
        }
        if (ratio.compareTo(maxPaymentCapacityRatio) > 0) {
            reasons.add(RecommendationReason.INSUFFICIENT_PAYMENT_CAPACITY);
        }
        Recommendation recommendation = reasons.isEmpty() ? Recommendation.APPROVE : Recommendation.REJECT;

        return new RiskAssessment(score, monthlyInstallment, monthlyIncome, ratio, recommendation, reasons, now);
    }

    /**
     * RN-07: los montos superiores al umbral requieren decisión del comité.
     */
    public boolean requiresCommittee(Money amount) {
        return amount.isGreaterThan(committeeThreshold);
    }

    /**
     * RN-08: fecha límite para subsanar una observación.
     */
    public Instant remediationDeadline(Instant observedAt) {
        return observedAt.plus(remediationDays, ChronoUnit.DAYS);
    }
}
