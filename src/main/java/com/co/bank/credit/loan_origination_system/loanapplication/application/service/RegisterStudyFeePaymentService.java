package com.co.bank.credit.loan_origination_system.loanapplication.application.service;

import com.co.bank.credit.loan_origination_system.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.RegisterStudyFeePaymentUseCase;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.RegisterStudyFeePaymentCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.out.LoanApplicationCommandPort;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplication;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import com.co.bank.credit.loan_origination_system.shared.application.port.out.PublishDomainEventsPort;

import java.time.Clock;
import java.util.Objects;

public class RegisterStudyFeePaymentService implements RegisterStudyFeePaymentUseCase {

    private final LoanApplicationCommandPort loanApplicationCommandPort;
    private final PublishDomainEventsPort publishDomainEventsPort;
    private final Clock clock;

    public RegisterStudyFeePaymentService(LoanApplicationCommandPort loanApplicationCommandPort,
                                          PublishDomainEventsPort publishDomainEventsPort, Clock clock) {
        this.loanApplicationCommandPort = Objects.requireNonNull(loanApplicationCommandPort,
                "loanApplicationCommandPort cannot be null");
        this.publishDomainEventsPort = Objects.requireNonNull(publishDomainEventsPort,
                "publishDomainEventsPort cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    @Override
    public void register(RegisterStudyFeePaymentCommand command) {
        Objects.requireNonNull(command, "command cannot be null");
        LoanApplicationId id = LoanApplicationId.of(command.loanApplicationId());
        LoanApplication application = loanApplicationCommandPort.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException(id));

        application.registerStudyFeePayment(Money.of(command.amount()), command.paymentReference(), clock.instant());

        loanApplicationCommandPort.save(application);
        publishDomainEventsPort.publishAll(application.pullDomainEvents());
    }
}
