package com.co.bank.credit.loan_origination_system.customer.application.service;

import com.co.bank.credit.loan_origination_system.customer.application.exception.CustomerNotFoundException;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.FindCustomerByIdUseCase;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.model.CustomerView;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.query.FindCustomerByIdQuery;
import com.co.bank.credit.loan_origination_system.customer.application.port.out.CustomerQueryPort;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;

import java.util.Objects;

public class FindCustomerByIdService implements FindCustomerByIdUseCase {

    private final CustomerQueryPort customerQueryPort;

    public FindCustomerByIdService(CustomerQueryPort customerQueryPort) {
        this.customerQueryPort = Objects.requireNonNull(customerQueryPort, "customerQueryPort cannot be null");
    }

    @Override
    public CustomerView findById(FindCustomerByIdQuery query) {
        Objects.requireNonNull(query, "query cannot be null");
        return customerQueryPort.findViewById(CustomerId.of(query.customerId()))
                .orElseThrow(() -> new CustomerNotFoundException(query.customerId()));
    }
}
