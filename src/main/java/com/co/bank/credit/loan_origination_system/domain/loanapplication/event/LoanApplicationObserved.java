package com.co.bank.credit.loan_origination_system.domain.loanapplication.event;

import com.co.bank.credit.loan_origination_system.domain.generic.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * El analista registró una observación; el cliente debe subsanarla antes de la fecha límite.
 */
public record LoanApplicationObserved(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber,
        String description,
        Instant remediationDueAt) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.observed";
    }
}
