package com.co.bank.credit.loan_origination_system.loanapplication.domain.event;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;

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
