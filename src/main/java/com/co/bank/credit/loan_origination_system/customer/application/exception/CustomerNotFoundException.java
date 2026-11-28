package com.co.bank.credit.loan_origination_system.customer.application.exception;

import com.co.bank.credit.loan_origination_system.shared.application.exception.ResourceNotFoundException;

public class CustomerNotFoundException extends ResourceNotFoundException {

    public CustomerNotFoundException(Object key) {
        super("Customer", key);
    }
}
