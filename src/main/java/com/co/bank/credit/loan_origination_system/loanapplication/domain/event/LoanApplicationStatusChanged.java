package com.co.bank.credit.loan_origination_system.loanapplication.domain.event;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationStatus;

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
