package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.time.Instant;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

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
