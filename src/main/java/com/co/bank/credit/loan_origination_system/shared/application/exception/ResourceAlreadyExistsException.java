package com.co.bank.credit.loan_origination_system.shared.application.exception;

public class ResourceAlreadyExistsException extends ApplicationException {

    public ResourceAlreadyExistsException(String resource, Object key) {
        super(resource + " already exists: " + key);
    }
}
