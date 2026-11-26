package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.time.Instant;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

/**
 * Pago confirmado de la tarifa de estudio de crédito.
 */
public record StudyFeePayment(Money amount, String paymentReference, Instant paidAt) {

    public StudyFeePayment {
        requireNonNull(amount, "Payment amount cannot be null");
        paymentReference = requireLength(
                requireNonBlank(paymentReference, "Payment reference cannot be null or blank"),
                1, 60, "Payment reference");
        requireNonNull(paidAt, "Payment date cannot be null");
    }
}
