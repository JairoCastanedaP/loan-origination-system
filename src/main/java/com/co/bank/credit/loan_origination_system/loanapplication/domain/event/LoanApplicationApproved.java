package com.co.bank.credit.loan_origination_system.loanapplication.domain.event;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

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
