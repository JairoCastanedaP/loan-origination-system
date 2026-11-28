package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in;

import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.SubmitLoanApplicationCommand;

/**
 * RF-07: radicar la solicitud.
 */
@FunctionalInterface
public interface SubmitLoanApplicationUseCase {

    void submit(SubmitLoanApplicationCommand command);
}
