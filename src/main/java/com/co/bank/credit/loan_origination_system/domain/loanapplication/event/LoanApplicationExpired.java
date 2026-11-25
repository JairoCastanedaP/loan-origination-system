package com.co.bank.credit.loan_origination_system.domain.loanapplication.event;

import com.co.bank.credit.loan_origination_system.domain.generic.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * La solicitud venció por no subsanar a tiempo.
 */
public record LoanApplicationExpired(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.expired";
    }
}
