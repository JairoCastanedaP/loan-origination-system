package com.co.bank.credit.loan_origination_system.loanapplication.domain.amortization;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.time.LocalDate;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

/**
 * Cuota del plan de pagos: lo que se paga, cuánto va a capital y a interés,
 * y el saldo que queda después del pago.
 */
public record Installment(int number, LocalDate dueDate, Money payment, Money principal, Money interest,
                          Money balance) {

    public Installment {
        if (number < 1) {
            throw new ValueObjectException("Installment number must be positive");
        }
        requireNonNull(dueDate, "Due date cannot be null");
        requireNonNull(payment, "Payment cannot be null");
        requireNonNull(principal, "Principal cannot be null");
        requireNonNull(interest, "Interest cannot be null");
        requireNonNull(balance, "Balance cannot be null");
    }
}
