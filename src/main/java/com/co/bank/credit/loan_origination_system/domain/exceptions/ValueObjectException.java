package com.co.bank.credit.loan_origination_system.domain.exceptions;

/**
 * Un Value Object recibió un valor inválido y no pudo construirse.
 */
public class ValueObjectException extends DomainException {

    public ValueObjectException(String message) {
        super(message);
    }
}
