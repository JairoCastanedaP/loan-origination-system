package com.co.bank.credit.loan_origination_system.domain.exceptions;

/**
 * Una entidad o agregado quedaría en un estado inconsistente.
 */
public class EntityException extends DomainException {

    public EntityException(String message) {
        super(message);
    }
}
