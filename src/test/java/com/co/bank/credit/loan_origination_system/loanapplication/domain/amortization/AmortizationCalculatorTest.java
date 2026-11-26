package com.co.bank.credit.loan_origination_system.loanapplication.domain.amortization;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.InterestRate;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AmortizationCalculatorTest {

    private static final InterestRate RATE_24_EA = InterestRate.ofAnnualPercentage("24");

    @Test
    void convertsAnnualEffectiveRateToMonthly() {
        // (1 + 0.24)^(1/12) - 1
        assertThat(RATE_24_EA.monthlyFraction()).isEqualByComparingTo("0.01808758248351072");
    }

    @Test
    void calculatesFrenchFixedInstallment() {
        // Valores de referencia calculados aparte con precisión de 40 dígitos
        assertThat(AmortizationCalculator.monthlyInstallment(Money.of("10000000"), RATE_24_EA, Term.ofMonths(12)))
                .isEqualTo(Money.of("934525.09"));
        assertThat(AmortizationCalculator.monthlyInstallment(Money.of("60000000"), RATE_24_EA, Term.ofMonths(60)))
                .isEqualTo(Money.of("1647090.15"));
    }

    @Test
    void paymentPlanPaysExactlyThePrincipal() {
        Money principal = Money.of("10000000");
        PaymentPlan plan = AmortizationCalculator.paymentPlan(principal, RATE_24_EA, Term.ofMonths(12),
                LocalDate.of(2026, 11, 25));

        assertThat(plan.size()).isEqualTo(12);
        assertThat(plan.totalPrincipal()).isEqualTo(principal);
        assertThat(plan.getInstallments().getLast().balance().isZero()).isTrue();
        assertThat(plan.totalPayment()).isEqualTo(plan.totalPrincipal().add(plan.totalInterest()));
        assertThat(plan.monthlyInstallment()).isEqualTo(Money.of("934525.09"));
    }

    @Test
    void firstInstallmentSplitsInterestAndPrincipal() {
        PaymentPlan plan = AmortizationCalculator.paymentPlan(Money.of("10000000"), RATE_24_EA, Term.ofMonths(12),
                LocalDate.of(2026, 11, 25));
        Installment first = plan.getInstallments().getFirst();

        assertThat(first.dueDate()).isEqualTo(LocalDate.of(2026, 12, 25));
        assertThat(first.interest()).isEqualTo(Money.of("180875.82"));
        assertThat(first.principal()).isEqualTo(Money.of("753649.27"));
        assertThat(first.balance()).isEqualTo(Money.of("9246350.73"));
    }
}
