package com.co.bank.credit.loan_origination_system.domain.loanapplication.event;

import com.co.bank.credit.loan_origination_system.domain.generic.DomainEvent;
import com.co.bank.credit.loan_origination_system.domain.shared.Money;

import java.time.Instant;
import java.util.UUID;

/**
 * El crédito fue desembolsado.
 */
public record LoanApplicationDisbursed(
        UUID eventId,
        Instant occurredOn,
        UUID applicationId,
        String applicationNumber,
        Money amount) implements DomainEvent {

    @Override
    public String eventType() {
        return "loan-application.disbursed";
    }
}
