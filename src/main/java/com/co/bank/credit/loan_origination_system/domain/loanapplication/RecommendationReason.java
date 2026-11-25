package com.co.bank.credit.loan_origination_system.domain.loanapplication;

/**
 * Motivos por los que la evaluación automática recomienda negar (RN-06).
 */
public enum RecommendationReason {

    INSUFFICIENT_SCORE("Score insuficiente"),
    INSUFFICIENT_PAYMENT_CAPACITY("Capacidad de pago insuficiente");

    private final String description;

    RecommendationReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
