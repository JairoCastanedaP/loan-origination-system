package com.co.bank.credit.loan_origination_system.creditproduct.application.exception;

import com.co.bank.credit.loan_origination_system.shared.application.exception.ResourceNotFoundException;

public class CreditProductNotFoundException extends ResourceNotFoundException {

    public CreditProductNotFoundException(Object id) {
        super("Credit product", id);
    }
}
