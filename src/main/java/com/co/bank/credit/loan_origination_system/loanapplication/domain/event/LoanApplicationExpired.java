package com.co.bank.credit.loan_origination_system.loanapplication.domain.event;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;

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
