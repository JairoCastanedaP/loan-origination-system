package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command;

import java.math.BigDecimal;
import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonBlank;
import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

public record RegisterStudyFeePaymentCommand(UUID loanApplicationId, BigDecimal amount, String paymentReference) {

    public RegisterStudyFeePaymentCommand {
        nonNull(loanApplicationId, "loanApplicationId");
        nonNull(amount, "amount");
        paymentReference = nonBlank(paymentReference, "paymentReference");
    }
}
