package com.co.bank.credit.loan_origination_system.creditproduct.application.port.in;

import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.command.CreateCreditProductCommand;

import java.util.UUID;

/**
 * RF-03: crear un producto de crédito.
 */
@FunctionalInterface
public interface CreateCreditProductUseCase {

    UUID create(CreateCreditProductCommand command);
}
