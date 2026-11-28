package com.co.bank.credit.loan_origination_system.customer.application.port.in;

import com.co.bank.credit.loan_origination_system.customer.application.port.in.command.RegisterCustomerCommand;

import java.util.UUID;

/**
 * RF-01: registrar un cliente.
 */
@FunctionalInterface
public interface RegisterCustomerUseCase {

    UUID register(RegisterCustomerCommand command);
}
