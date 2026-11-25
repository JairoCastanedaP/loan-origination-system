package com.co.bank.credit.loan_origination_system.domain.loanapplication.event;

import com.co.bank.credit.loan_origination_system.domain.generic.DomainEvent;
import com.co.bank.credit.loan_origination_system.domain.shared.Money;

import java.time.Instant;
import java.util.UUID;

/**
 * La solicitud fue aprobada y tiene plan de pagos.
 */
public record LoanApplicationApproved(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber,
        Money amount,
        Money monthlyInstallment,
        boolean byCommittee) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.approved";
    }
}
