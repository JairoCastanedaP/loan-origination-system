package com.co.bank.credit.loan_origination_system.customer.application.service;

import com.co.bank.credit.loan_origination_system.customer.application.exception.CustomerAlreadyExistsException;
import com.co.bank.credit.loan_origination_system.customer.application.mapper.CustomerApplicationMapper;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.RegisterCustomerUseCase;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.command.RegisterCustomerCommand;
import com.co.bank.credit.loan_origination_system.customer.application.port.out.CustomerCommandPort;
import com.co.bank.credit.loan_origination_system.customer.domain.Customer;

import java.util.Objects;
import java.util.UUID;

public class RegisterCustomerService implements RegisterCustomerUseCase {

    private final CustomerCommandPort customerCommandPort;

    public RegisterCustomerService(CustomerCommandPort customerCommandPort) {
        this.customerCommandPort = Objects.requireNonNull(customerCommandPort, "customerCommandPort cannot be null");
    }

    @Override
    public UUID register(RegisterCustomerCommand command) {
        Objects.requireNonNull(command, "command cannot be null");
        Customer customer = CustomerApplicationMapper.toDomain(command);
        if (customerCommandPort.existsByDocument(customer.getDocument())) {
            throw new CustomerAlreadyExistsException(customer.getDocument());
        }
        customerCommandPort.save(customer);
        return customer.getId().getValue();
    }
}
