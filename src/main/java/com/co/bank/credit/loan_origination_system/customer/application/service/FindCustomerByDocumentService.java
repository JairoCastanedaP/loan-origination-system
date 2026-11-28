package com.co.bank.credit.loan_origination_system.customer.application.service;

import com.co.bank.credit.loan_origination_system.customer.application.exception.CustomerNotFoundException;
import com.co.bank.credit.loan_origination_system.customer.application.mapper.CustomerApplicationMapper;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.FindCustomerByDocumentUseCase;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.model.CustomerView;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.query.FindCustomerByDocumentQuery;
import com.co.bank.credit.loan_origination_system.customer.application.port.out.CustomerQueryPort;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;

import java.util.Objects;

public class FindCustomerByDocumentService implements FindCustomerByDocumentUseCase {

    private final CustomerQueryPort customerQueryPort;

    public FindCustomerByDocumentService(CustomerQueryPort customerQueryPort) {
        this.customerQueryPort = Objects.requireNonNull(customerQueryPort, "customerQueryPort cannot be null");
    }

    @Override
    public CustomerView findByDocument(FindCustomerByDocumentQuery query) {
        Objects.requireNonNull(query, "query cannot be null");
        DocumentNumber document = CustomerApplicationMapper.toDocument(query.documentType(), query.documentNumber());
        return customerQueryPort.findViewByDocument(document)
                .orElseThrow(() -> new CustomerNotFoundException(document));
    }
}
