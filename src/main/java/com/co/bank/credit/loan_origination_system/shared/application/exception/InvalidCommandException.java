package com.co.bank.credit.loan_origination_system.shared.application.exception;

/**
 * Un Command o Query llegó sin un dato obligatorio.
 */
public class InvalidCommandException extends ApplicationException {

    public InvalidCommandException(String message) {
        super(message);
    }
}
