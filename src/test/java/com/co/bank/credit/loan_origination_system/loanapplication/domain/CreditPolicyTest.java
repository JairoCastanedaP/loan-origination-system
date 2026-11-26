package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Casos límite de RN-05 a RN-08 con la política estándar (score 500, capacidad 40%, comité > 50M, 5 días).
 */
class CreditPolicyTest {

    private static final CreditPolicy POLICY = CreditPolicy.standard();
    private static final Instant NOW = Instant.parse("2026-11-25T15:00:00Z");
    private static final Money INCOME = Money.of("1000000");

    private static RiskAssessment assess(int score, String installment) {
        return POLICY.assess(CreditScore.of(score), Money.of(installment), INCOME, NOW);
    }

    @Test
    void standardPolicyHasTheApprovedValues() {
        assertThat(POLICY.minimumScore()).isEqualTo(500);
        assertThat(POLICY.maxPaymentCapacityRatio()).isEqualByComparingTo("0.40");
        assertThat(POLICY.committeeThreshold()).isEqualTo(Money.of("50000000"));
        assertThat(POLICY.remediationDays()).isEqualTo(5);
    }

    @ParameterizedTest(name = "score {0}, cuota {1} -> {2}")
    @CsvSource({
            "500, 400000, APPROVE",   // justo en ambos umbrales
            "499, 400000, REJECT",    // un punto por debajo del score
            "500, 400100, REJECT",    // 40,01% de la cuota sobre el ingreso
            "500, 400040, APPROVE",   // 0,40004 se redondea a 0,4000 (4 decimales)
            "1000, 0.01, APPROVE"
    })
    void recommendationOnTheThresholds(int score, String installment, Recommendation expected) {
        assertThat(assess(score, installment).recommendation()).isEqualTo(expected);
    }

    @Test
    void reportsEveryReasonForRejection() {
        RiskAssessment assessment = assess(320, "550000");

        assertThat(assessment.paymentCapacityRatio()).isEqualByComparingTo("0.55");
        assertThat(assessment.reasons()).containsExactly(
                RecommendationReason.INSUFFICIENT_SCORE, RecommendationReason.INSUFFICIENT_PAYMENT_CAPACITY);
        assertThat(assessment.assessedAt()).isEqualTo(NOW);
    }

    @Test
    void approvalHasNoReasons() {
        assertThat(assess(720, "300000").reasons()).isEmpty();
    }

    @ParameterizedTest(name = "{0} -> comité {1}")
    @CsvSource({"49999999.99,false", "50000000,false", "50000000.01,true", "120000000,true"})
    void rn07CommitteeOnlyAboveTheThreshold(String amount, boolean expected) {
        assertThat(POLICY.requiresCommittee(Money.of(amount))).isEqualTo(expected);
    }

    @Test
    void rn08DeadlineIsFiveDaysAfterTheObservation() {
        assertThat(POLICY.remediationDeadline(NOW)).isEqualTo(NOW.plus(Duration.ofDays(5)));
    }

    @Test
    void incomeMustBePositive() {
        assertThatThrownBy(() -> POLICY.assess(CreditScore.of(700), Money.of("100"), Money.zero(), NOW))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void rejectsInvalidParameters() {
        Money threshold = Money.of("50000000");

        assertThatThrownBy(() -> new CreditPolicy(1001, new BigDecimal("0.40"), threshold, 5))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> new CreditPolicy(500, BigDecimal.ZERO, threshold, 5))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> new CreditPolicy(500, new BigDecimal("1.01"), threshold, 5))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> new CreditPolicy(500, new BigDecimal("0.40"), null, 5))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> new CreditPolicy(500, new BigDecimal("0.40"), threshold, 0))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> new CreditPolicy(500, new BigDecimal("0.40"), threshold, 91))
                .isInstanceOf(ValueObjectException.class);
    }
}
