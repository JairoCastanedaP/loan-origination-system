package com.co.bank.credit.loan_origination_system.loanapplication.domain.event;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/**
 * La solicitud fue negada.
 */
public record LoanApplicationRejected(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber,
        String reason) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.rejected";
    }
}
