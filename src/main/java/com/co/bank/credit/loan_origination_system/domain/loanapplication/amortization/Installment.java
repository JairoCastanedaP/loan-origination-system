package com.co.bank.credit.loan_origination_system.domain.loanapplication.amortization;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;
import com.co.bank.credit.loan_origination_system.domain.shared.Money;

import java.time.LocalDate;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

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
