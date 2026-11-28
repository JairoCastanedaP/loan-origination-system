package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command;

import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

public record SubmitLoanApplicationCommand(UUID loanApplicationId) {

    public SubmitLoanApplicationCommand {
        nonNull(loanApplicationId, "loanApplicationId");
    }
}
