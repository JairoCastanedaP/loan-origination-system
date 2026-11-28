package com.co.bank.credit.loan_origination_system.shared.application.exception;

public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " not found: " + id);
    }
}
