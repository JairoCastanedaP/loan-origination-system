package com.co.bank.credit.loan_origination_system.loanapplication.application.service;

import com.co.bank.credit.loan_origination_system.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.AttachRequirementUseCase;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.AttachRequirementCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.out.LoanApplicationCommandPort;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplication;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;
import com.co.bank.credit.loan_origination_system.shared.application.port.out.PublishDomainEventsPort;

import java.time.Clock;
import java.util.Objects;

public class AttachRequirementService implements AttachRequirementUseCase {

    private final LoanApplicationCommandPort loanApplicationCommandPort;
    private final PublishDomainEventsPort publishDomainEventsPort;
    private final Clock clock;

    public AttachRequirementService(LoanApplicationCommandPort loanApplicationCommandPort,
                                    PublishDomainEventsPort publishDomainEventsPort, Clock clock) {
        this.loanApplicationCommandPort = Objects.requireNonNull(loanApplicationCommandPort,
                "loanApplicationCommandPort cannot be null");
        this.publishDomainEventsPort = Objects.requireNonNull(publishDomainEventsPort,
                "publishDomainEventsPort cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    @Override
    public void attach(AttachRequirementCommand command) {
        Objects.requireNonNull(command, "command cannot be null");
        LoanApplicationId id = LoanApplicationId.of(command.loanApplicationId());
        LoanApplication application = loanApplicationCommandPort.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException(id));

        application.attachRequirement(RequirementType.of(command.requirementType()), command.documentReference(),
                clock.instant());

        loanApplicationCommandPort.save(application);
        publishDomainEventsPort.publishAll(application.pullDomainEvents());
    }
}
