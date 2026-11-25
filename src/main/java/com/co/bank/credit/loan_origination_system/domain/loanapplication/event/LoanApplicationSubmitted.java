package com.co.bank.credit.loan_origination_system.domain.loanapplication.event;

import com.co.bank.credit.loan_origination_system.domain.generic.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * La solicitud fue radicada con todos sus requisitos.
 */
public record LoanApplicationSubmitted(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber,
        UUID customerId) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.submitted";
    }
}
