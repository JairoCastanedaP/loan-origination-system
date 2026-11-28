package com.co.bank.credit.loan_origination_system.loanapplication.application.service;

import com.co.bank.credit.loan_origination_system.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.SubmitLoanApplicationUseCase;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.SubmitLoanApplicationCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.out.LoanApplicationCommandPort;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplication;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;
import com.co.bank.credit.loan_origination_system.shared.application.port.out.PublishDomainEventsPort;

import java.time.Clock;
import java.util.Objects;

public class SubmitLoanApplicationService implements SubmitLoanApplicationUseCase {

    private final LoanApplicationCommandPort loanApplicationCommandPort;
    private final PublishDomainEventsPort publishDomainEventsPort;
    private final Clock clock;

    public SubmitLoanApplicationService(LoanApplicationCommandPort loanApplicationCommandPort,
                                        PublishDomainEventsPort publishDomainEventsPort, Clock clock) {
        this.loanApplicationCommandPort = Objects.requireNonNull(loanApplicationCommandPort,
                "loanApplicationCommandPort cannot be null");
        this.publishDomainEventsPort = Objects.requireNonNull(publishDomainEventsPort,
                "publishDomainEventsPort cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    @Override
    public void submit(SubmitLoanApplicationCommand command) {
        Objects.requireNonNull(command, "command cannot be null");
        LoanApplicationId id = LoanApplicationId.of(command.loanApplicationId());
        LoanApplication application = loanApplicationCommandPort.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException(id));

        application.submit(clock.instant());

        loanApplicationCommandPort.save(application);
        publishDomainEventsPort.publishAll(application.pullDomainEvents());
    }
}
