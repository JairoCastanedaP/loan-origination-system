package com.co.bank.credit.loan_origination_system.customer.application.exception;

import com.co.bank.credit.loan_origination_system.shared.application.exception.ResourceAlreadyExistsException;

public class CustomerAlreadyExistsException extends ResourceAlreadyExistsException {

    public CustomerAlreadyExistsException(Object document) {
        super("Customer with document", document);
    }
}
