package com.co.bank.credit.loan_origination_system.loanapplication.domain.amortization;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.InterestRate;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

/**
 * Servicio de dominio para el sistema de amortización francés (cuota fija), RN-04.
 * <p>
 * Cuota = P · r / (1 − (1 + r)^−n), con r = tasa mensual equivalente y n = plazo en meses.
 * Cada mes: interés = saldo · r; capital = cuota − interés. La última cuota abona
 * todo el saldo restante, así la suma del capital es exactamente igual al monto prestado.
 */
public final class AmortizationCalculator {

    private static final MathContext PRECISION = new MathContext(20, RoundingMode.HALF_EVEN);

    private AmortizationCalculator() {
    }

    public static Money monthlyInstallment(Money principal, InterestRate rate, Term term) {
        requireNonNull(principal, "Principal cannot be null");
        requireNonNull(rate, "Interest rate cannot be null");
        requireNonNull(term, "Term cannot be null");

        BigDecimal r = rate.monthlyFraction();
        BigDecimal factor = BigDecimal.ONE.add(r).pow(term.getMonths(), PRECISION);
        BigDecimal payment = principal.getAmount()
                .multiply(r, PRECISION)
                .multiply(factor, PRECISION)
                .divide(factor.subtract(BigDecimal.ONE), PRECISION);
        return Money.of(payment, principal.getCurrency());
    }

    /**
     * @param startDate fecha base; la cuota i vence startDate + i meses
     */
    public static PaymentPlan paymentPlan(Money principal, InterestRate rate, Term term, LocalDate startDate) {
        requireNonNull(startDate, "Start date cannot be null");
        Money payment = monthlyInstallment(principal, rate, term);
        BigDecimal r = rate.monthlyFraction();

        List<Installment> installments = new ArrayList<>(term.getMonths());
        Money balance = principal;
        for (int i = 1; i <= term.getMonths(); i++) {
            Money interest = balance.multiply(r);
            boolean last = i == term.getMonths();
            Money principalPart = last ? balance : payment.subtract(interest);
            Money installmentPayment = last ? principalPart.add(interest) : payment;
            balance = balance.subtract(principalPart);
            installments.add(new Installment(i, startDate.plusMonths(i), installmentPayment, principalPart,
                    interest, balance));
        }
        return PaymentPlan.of(installments);
    }
}
