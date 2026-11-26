package com.co.bank.credit.loan_origination_system.loanapplication.domain.event;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

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
