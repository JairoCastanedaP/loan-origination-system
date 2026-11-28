package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in;

import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.AttachRequirementCommand;

/**
 * RF-06: cargar un requisito documental.
 */
@FunctionalInterface
public interface AttachRequirementUseCase {

    void attach(AttachRequirementCommand command);
}
