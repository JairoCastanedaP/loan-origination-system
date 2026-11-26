package com.co.bank.credit.loan_origination_system.loanapplication.domain.amortization;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.util.List;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

/**
 * Plan de pagos (tabla de amortización) de un crédito aprobado.
 */
public final class PaymentPlan {

    private final List<Installment> installments;

    private PaymentPlan(List<Installment> installments) {
        this.installments = installments;
    }

    public static PaymentPlan of(List<Installment> installments) {
        requireNonNull(installments, "Installments cannot be null");
        if (installments.isEmpty()) {
            throw new ValueObjectException("A payment plan must have at least one installment");
        }
        return new PaymentPlan(List.copyOf(installments));
    }

    public List<Installment> getInstallments() {
        return installments;
    }

    public int size() {
        return installments.size();
    }

    /**
     * Cuota fija del plan (la última puede variar unos centavos por el ajuste de redondeo).
     */
    public Money monthlyInstallment() {
        return installments.getFirst().payment();
    }

    public Money totalPayment() {
        return installments.stream().map(Installment::payment).reduce(Money.zero(), Money::add);
    }

    public Money totalPrincipal() {
        return installments.stream().map(Installment::principal).reduce(Money.zero(), Money::add);
    }

    public Money totalInterest() {
        return installments.stream().map(Installment::interest).reduce(Money.zero(), Money::add);
    }
}
