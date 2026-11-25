package com.co.bank.credit.loan_origination_system.domain.loanapplication.event;

import com.co.bank.credit.loan_origination_system.domain.generic.DomainEvent;
import com.co.bank.credit.loan_origination_system.domain.loanapplication.LoanApplicationStatus;

import java.time.Instant;
import java.util.UUID;

/**
 * Se emite en cada cambio de estado; alimenta el seguimiento de la solicitud.
 */
public record LoanApplicationStatusChanged(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber,
        LoanApplicationStatus from,
        LoanApplicationStatus to) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.status-changed";
    }
}
