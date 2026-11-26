package com.co.bank.credit.loan_origination_system.loanapplication.domain.event;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;

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
