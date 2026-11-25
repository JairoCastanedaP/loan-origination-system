package com.co.bank.credit.loan_origination_system.domain.loanapplication.event;

import com.co.bank.credit.loan_origination_system.domain.generic.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * El cliente subsanó las observaciones dentro del plazo.
 */
public record LoanApplicationRemediated(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.remediated";
    }
}
