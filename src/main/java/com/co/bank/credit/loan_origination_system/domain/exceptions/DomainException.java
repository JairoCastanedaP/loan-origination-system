package com.co.bank.credit.loan_origination_system.domain.exceptions;

/**
 * Base de todas las excepciones del dominio.
 * Es unchecked para que el dominio no obligue a las capas externas a declararla.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
