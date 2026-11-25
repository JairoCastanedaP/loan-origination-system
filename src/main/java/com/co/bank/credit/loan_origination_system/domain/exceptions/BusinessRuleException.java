package com.co.bank.credit.loan_origination_system.domain.exceptions;

/**
 * Se incumplió una regla de negocio (RN-xx). El código permite rastrear la regla
 * y traducirla a una respuesta en los adaptadores de entrada.
 */
public class BusinessRuleException extends DomainException {

    private final String ruleCode;

    public BusinessRuleException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() {
        return ruleCode;
    }
}
