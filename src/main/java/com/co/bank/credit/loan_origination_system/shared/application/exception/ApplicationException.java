package com.co.bank.credit.loan_origination_system.shared.application.exception;

/**
 * Error de la capa de aplicación: datos de entrada incompletos, recurso inexistente o duplicado.
 * Los errores de reglas de negocio son {@code DomainException} y los lanza el dominio.
 */
public class ApplicationException extends RuntimeException {

    public ApplicationException(String message) {
        super(message);
    }
}
