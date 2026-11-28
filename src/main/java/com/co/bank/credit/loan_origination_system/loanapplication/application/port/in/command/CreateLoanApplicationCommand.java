package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command;

import java.math.BigDecimal;
import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

public record CreateLoanApplicationCommand(
        UUID customerId,
        UUID creditProductId,
        BigDecimal requestedAmount,
        Integer termMonths) {

    public CreateLoanApplicationCommand {
        nonNull(customerId, "customerId");
        nonNull(creditProductId, "creditProductId");
        nonNull(requestedAmount, "requestedAmount");
        nonNull(termMonths, "termMonths");
    }
}
