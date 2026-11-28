package com.co.bank.credit.loan_origination_system.loanapplication.application.service;

import com.co.bank.credit.loan_origination_system.creditproduct.application.exception.CreditProductNotFoundException;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.out.CreditProductCommandPort;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.customer.application.exception.CustomerNotFoundException;
import com.co.bank.credit.loan_origination_system.customer.application.port.out.CustomerCommandPort;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.CreateLoanApplicationUseCase;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.CreateLoanApplicationCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.model.LoanApplicationReference;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.out.ApplicationNumberGeneratorPort;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.out.LoanApplicationCommandPort;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.ApplicationNumber;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplication;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;
import com.co.bank.credit.loan_origination_system.shared.application.port.out.PublishDomainEventsPort;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Crea la solicitud en borrador. Valida, en este orden, que existan el cliente y el producto,
 * RN-12 (una sola solicitud abierta por cliente y producto) y las condiciones del producto,
 * antes de tomar un número consecutivo, para no gastar números en solicitudes rechazadas.
 */
public class CreateLoanApplicationService implements CreateLoanApplicationUseCase {

    private final LoanApplicationCommandPort loanApplicationCommandPort;
    private final ApplicationNumberGeneratorPort applicationNumberGeneratorPort;
    private final CustomerCommandPort customerCommandPort;
    private final CreditProductCommandPort creditProductCommandPort;
    private final PublishDomainEventsPort publishDomainEventsPort;
    private final Clock clock;

    public CreateLoanApplicationService(LoanApplicationCommandPort loanApplicationCommandPort,
                                        ApplicationNumberGeneratorPort applicationNumberGeneratorPort,
                                        CustomerCommandPort customerCommandPort,
                                        CreditProductCommandPort creditProductCommandPort,
                                        PublishDomainEventsPort publishDomainEventsPort,
                                        Clock clock) {
        this.loanApplicationCommandPort = Objects.requireNonNull(loanApplicationCommandPort,
                "loanApplicationCommandPort cannot be null");
        this.applicationNumberGeneratorPort = Objects.requireNonNull(applicationNumberGeneratorPort,
                "applicationNumberGeneratorPort cannot be null");
        this.customerCommandPort = Objects.requireNonNull(customerCommandPort, "customerCommandPort cannot be null");
        this.creditProductCommandPort = Objects.requireNonNull(creditProductCommandPort,
                "creditProductCommandPort cannot be null");
        this.publishDomainEventsPort = Objects.requireNonNull(publishDomainEventsPort,
                "publishDomainEventsPort cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    @Override
    public LoanApplicationReference create(CreateLoanApplicationCommand command) {
        Objects.requireNonNull(command, "command cannot be null");
        CustomerId customerId = CustomerId.of(command.customerId());
        CreditProductId productId = CreditProductId.of(command.creditProductId());
        Money amount = Money.of(command.requestedAmount());
        Term term = Term.ofMonths(command.termMonths());

        if (customerCommandPort.findById(customerId).isEmpty()) {
            throw new CustomerNotFoundException(customerId);
        }
        CreditProduct product = creditProductCommandPort.findById(productId)
                .orElseThrow(() -> new CreditProductNotFoundException(productId));
        if (loanApplicationCommandPort.existsOpenApplication(customerId, productId)) {
            throw new BusinessRuleException("RN-12",
                    "Customer " + customerId + " already has an open application for product " + product.getName());
        }
        product.ensureActive();
        product.validateConditions(amount, term);

        Instant now = clock.instant();
        ApplicationNumber number = applicationNumberGeneratorPort.next(LocalDate.ofInstant(now, clock.getZone()).getYear());
        LoanApplication application = LoanApplication.create(LoanApplicationId.generate(), number, customerId,
                product, amount, term, now);

        loanApplicationCommandPort.save(application);
        publishDomainEventsPort.publishAll(application.pullDomainEvents());
        return new LoanApplicationReference(application.getId().getValue(), number.getValue());
    }
}
