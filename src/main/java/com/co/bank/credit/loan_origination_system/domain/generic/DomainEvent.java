package com.co.bank.credit.loan_origination_system.domain.generic;

import java.time.Instant;
import java.util.UUID;

/**
 * Hecho relevante que ocurrió en el dominio.
 * Cada evento concreto define su tipo y los datos que necesita transportar.
 */
public interface DomainEvent {

    UUID eventId();

    Instant occurredOn();

    String eventType();

    default String systemName() {
        return "Loan Origination System";
    }

    default String version() {
        return "v1";
    }
}
