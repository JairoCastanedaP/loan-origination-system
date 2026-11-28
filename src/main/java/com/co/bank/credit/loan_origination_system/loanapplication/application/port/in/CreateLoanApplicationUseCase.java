package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in;

import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.CreateLoanApplicationCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.model.LoanApplicationReference;

/**
 * RF-04: crear una solicitud en borrador.
 */
@FunctionalInterface
public interface CreateLoanApplicationUseCase {

    LoanApplicationReference create(CreateLoanApplicationCommand command);
}
