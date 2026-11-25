package com.co.bank.credit.loan_origination_system.domain.loanapplication;

import com.co.bank.credit.loan_origination_system.domain.exceptions.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * Ciclo de vida de una solicitud de crédito. Cualquier transición que no esté en
 * {@link #allowedTransitions()} es rechazada.
 */
public enum LoanApplicationStatus {

    DRAFT("Borrador, editable por el cliente"),
    SUBMITTED("Radicada, pendiente del pago del estudio"),
    UNDER_REVIEW("En estudio por el analista"),
    OBSERVED("Con observaciones pendientes de subsanar"),
    REMEDIATED("Subsanada, pendiente de reanudar el estudio"),
    IN_COMMITTEE("En decisión del comité"),
    APPROVED("Aprobada, pendiente de desembolso"),
    REJECTED("Negada"),
    DISBURSED("Desembolsada"),
    EXPIRED("Vencida por no subsanar a tiempo"),
    CANCELLED("Cancelada por el cliente");

    private final String description;

    LoanApplicationStatus(String description) {
        this.description = description;
    }

    public static LoanApplicationStatus of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("Loan application status cannot be null or blank");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid loan application status: " + value);
        }
    }

    public Set<LoanApplicationStatus> allowedTransitions() {
        return switch (this) {
            case DRAFT -> EnumSet.of(SUBMITTED, CANCELLED);
            case SUBMITTED -> EnumSet.of(UNDER_REVIEW, CANCELLED);
            case UNDER_REVIEW -> EnumSet.of(OBSERVED, IN_COMMITTEE, APPROVED, REJECTED);
            case OBSERVED -> EnumSet.of(REMEDIATED, EXPIRED);
            case REMEDIATED -> EnumSet.of(UNDER_REVIEW);
            case IN_COMMITTEE -> EnumSet.of(APPROVED, REJECTED);
            case APPROVED -> EnumSet.of(DISBURSED);
            case REJECTED, DISBURSED, EXPIRED, CANCELLED -> EnumSet.noneOf(LoanApplicationStatus.class);
        };
    }

    public boolean canTransitionTo(LoanApplicationStatus target) {
        return target != null && allowedTransitions().contains(target);
    }

    public LoanApplicationStatus transitionTo(LoanApplicationStatus target) {
        if (target == null) {
            throw new ValueObjectException("Target status cannot be null");
        }
        if (!canTransitionTo(target)) {
            throw new BusinessRuleException("INVALID_TRANSITION",
                    "Invalid status transition from " + name() + " to " + target.name());
        }
        return target;
    }

    public boolean isTerminal() {
        return allowedTransitions().isEmpty();
    }

    public String getDescription() {
        return description;
    }
}
