package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in;

import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.RegisterStudyFeePaymentCommand;

/**
 * RF-08: registrar el pago de la tarifa de estudio.
 */
@FunctionalInterface
public interface RegisterStudyFeePaymentUseCase {

    void register(RegisterStudyFeePaymentCommand command);
}
