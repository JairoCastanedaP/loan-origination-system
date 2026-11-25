package com.co.bank.credit.loan_origination_system.domain.loanapplication;

import com.co.bank.credit.loan_origination_system.domain.shared.Money;

import java.time.Instant;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Registro del desembolso del crédito aprobado.
 */
public record Disbursement(Money amount, String disbursedBy, Instant disbursedAt) {

    public Disbursement {
        requireNonNull(amount, "Disbursement amount cannot be null");
        disbursedBy = requireNonBlank(disbursedBy, "Disbursed by cannot be null or blank");
        requireNonNull(disbursedAt, "Disbursement date cannot be null");
    }
}
