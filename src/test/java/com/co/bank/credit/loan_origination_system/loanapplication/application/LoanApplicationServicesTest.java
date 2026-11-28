package com.co.bank.credit.loan_origination_system.loanapplication.application;

import com.co.bank.credit.loan_origination_system.creditproduct.application.InMemoryCreditProductStore;
import com.co.bank.credit.loan_origination_system.creditproduct.application.exception.CreditProductNotFoundException;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.InterestRate;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.customer.application.InMemoryCustomerStore;
import com.co.bank.credit.loan_origination_system.customer.application.exception.CustomerNotFoundException;
import com.co.bank.credit.loan_origination_system.customer.domain.Customer;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.loanapplication.application.exception.LoanApplicationNotFoundException;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.AttachRequirementCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.CreateLoanApplicationCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.RegisterStudyFeePaymentCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command.SubmitLoanApplicationCommand;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.model.LoanApplicationReference;
import com.co.bank.credit.loan_origination_system.loanapplication.application.service.AttachRequirementService;
import com.co.bank.credit.loan_origination_system.loanapplication.application.service.CreateLoanApplicationService;
import com.co.bank.credit.loan_origination_system.loanapplication.application.service.RegisterStudyFeePaymentService;
import com.co.bank.credit.loan_origination_system.loanapplication.application.service.SubmitLoanApplicationService;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplication;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationStatus;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationStatusChanged;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationSubmitted;
import com.co.bank.credit.loan_origination_system.shared.application.RecordingEventPublisher;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentType;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Email;
import com.co.bank.credit.loan_origination_system.shared.domain.model.FirstName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.LastName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import com.co.bank.credit.loan_origination_system.shared.domain.model.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Primer tramo del flujo: crear, cargar requisitos, radicar y pagar el estudio (RF-04 a RF-08).
 */
class LoanApplicationServicesTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");
    private static final Instant NOW = Instant.parse("2026-11-27T15:00:00Z");
    private static final BigDecimal STUDY_FEE = new BigDecimal("80000");

    private final InMemoryCustomerStore customers = new InMemoryCustomerStore();
    private final InMemoryCreditProductStore products = new InMemoryCreditProductStore();
    private final InMemoryLoanApplicationStore applications = new InMemoryLoanApplicationStore();
    private final RecordingEventPublisher events = new RecordingEventPublisher();
    private final Clock clock = Clock.fixed(NOW, BOGOTA);

    private CustomerId customerId;
    private CreditProduct personalLoan;

    private CreateLoanApplicationService createService(Clock clock) {
        return new CreateLoanApplicationService(applications, applications, customers, products, events, clock);
    }

    private final AttachRequirementService attach = new AttachRequirementService(applications, events, clock);
    private final SubmitLoanApplicationService submit = new SubmitLoanApplicationService(applications, events, clock);
    private final RegisterStudyFeePaymentService pay =
            new RegisterStudyFeePaymentService(applications, events, clock);

    @BeforeEach
    void setUp() {
        customerId = registerCustomer("1020304050");
        personalLoan = CreditProduct.create(CreditProductId.generate(), "Libre inversión",
                CreditProductType.PERSONAL_LOAN, InterestRate.ofAnnualPercentage("24"), Money.of("1000000"),
                Money.of("100000000"), Term.ofMonths(12), Term.ofMonths(60), Money.of(STUDY_FEE),
                EnumSet.of(RequirementType.IDENTITY_DOCUMENT, RequirementType.EMPLOYMENT_CERTIFICATE));
        products.save(personalLoan);
    }

    private CustomerId registerCustomer(String document) {
        Customer customer = Customer.create(CustomerId.generate(), DocumentNumber.of(DocumentType.CC, document),
                FirstName.of("Laura"), LastName.of("Gómez"), Email.of("laura@example.com"),
                PhoneNumber.of("3001234567"), Money.of("5000000"));
        customers.save(customer);
        return customer.getId();
    }

    private LoanApplicationReference create(CustomerId customer, String amount, int months) {
        return createService(clock).create(new CreateLoanApplicationCommand(customer.getValue(),
                personalLoan.getId().getValue(), new BigDecimal(amount), months));
    }

    private LoanApplication stored(LoanApplicationReference reference) {
        return applications.findById(LoanApplicationId.of(reference.id())).orElseThrow();
    }

    @Test
    void happyPathUntilUnderReview() {
        LoanApplicationReference reference = create(customerId, "10000000", 24);
        UUID id = reference.id();

        attach.attach(new AttachRequirementCommand(id, "identity_document", "cc-laura.pdf"));
        attach.attach(new AttachRequirementCommand(id, "EMPLOYMENT_CERTIFICATE", "certificado.pdf"));
        submit.submit(new SubmitLoanApplicationCommand(id));
        pay.register(new RegisterStudyFeePaymentCommand(id, STUDY_FEE, "PSE-123"));

        LoanApplication application = stored(reference);
        assertThat(reference.applicationNumber()).isEqualTo("LOS-2026-000001");
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.UNDER_REVIEW);
        assertThat(application.getStudyFeePayment()).get()
                .satisfies(p -> assertThat(p.paidAt()).isEqualTo(NOW));
        assertThat(events.published(LoanApplicationSubmitted.class)).singleElement()
                .satisfies(e -> assertThat(e.customerId()).isEqualTo(customerId.getValue()));
        assertThat(events.published(LoanApplicationStatusChanged.class)).extracting(LoanApplicationStatusChanged::to)
                .containsExactly(LoanApplicationStatus.SUBMITTED, LoanApplicationStatus.UNDER_REVIEW);
        assertThat(application.hasDomainEvents()).isFalse();
    }

    @Test
    void rf05NumbersAreConsecutivePerYear() {
        LoanApplicationReference first = create(customerId, "10000000", 24);
        LoanApplicationReference second = create(registerCustomer("80706050"), "5000000", 12);

        assertThat(first.applicationNumber()).isEqualTo("LOS-2026-000001");
        assertThat(second.applicationNumber()).isEqualTo("LOS-2026-000002");
    }

    @Test
    void rf05YearUsesTheLocalTimeZone() {
        // 2027-01-01 04:30 UTC todavía es 31 de diciembre de 2026 en Bogotá
        Clock newYearsEve = Clock.fixed(Instant.parse("2027-01-01T04:30:00Z"), BOGOTA);

        LoanApplicationReference reference = createService(newYearsEve).create(new CreateLoanApplicationCommand(
                customerId.getValue(), personalLoan.getId().getValue(), new BigDecimal("10000000"), 24));

        assertThat(reference.applicationNumber()).startsWith("LOS-2026-");
    }

    @Test
    void rn12OnlyOneOpenApplicationPerCustomerAndProduct() {
        LoanApplicationReference first = create(customerId, "10000000", 24);

        assertThatThrownBy(() -> create(customerId, "3000000", 12))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-12");
        assertThat(applications.lastSequence(2026)).isEqualTo(1);

        stored(first).cancel("Prefiero otro monto", NOW);
        assertThat(create(customerId, "3000000", 12).applicationNumber()).isEqualTo("LOS-2026-000002");
    }

    @Test
    void rejectedRequestsDoNotConsumeNumbers() {
        assertThatThrownBy(() -> create(customerId, "500000", 24))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-01");

        personalLoan.deactivate();
        assertThatThrownBy(() -> create(customerId, "10000000", 24))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("PRODUCT_INACTIVE");

        assertThat(applications.lastSequence(2026)).isZero();
        assertThat(applications.saves()).isZero();
    }

    @Test
    void customerAndProductMustExist() {
        assertThatThrownBy(() -> create(CustomerId.generate(), "10000000", 24))
                .isInstanceOf(CustomerNotFoundException.class);
        assertThatThrownBy(() -> createService(clock).create(new CreateLoanApplicationCommand(
                customerId.getValue(), UUID.randomUUID(), new BigDecimal("10000000"), 24)))
                .isInstanceOf(CreditProductNotFoundException.class);
    }

    @Test
    void rn02CannotSubmitWithMissingRequirements() {
        UUID id = create(customerId, "10000000", 24).id();
        attach.attach(new AttachRequirementCommand(id, "IDENTITY_DOCUMENT", "cc.pdf"));
        events.clear();

        assertThatThrownBy(() -> submit.submit(new SubmitLoanApplicationCommand(id)))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-02");
        assertThat(events.published()).isEmpty();
    }

    @Test
    void rn03PaymentMustMatchTheStudyFee() {
        UUID id = create(customerId, "10000000", 24).id();
        attach.attach(new AttachRequirementCommand(id, "IDENTITY_DOCUMENT", "cc.pdf"));
        attach.attach(new AttachRequirementCommand(id, "EMPLOYMENT_CERTIFICATE", "cert.pdf"));
        submit.submit(new SubmitLoanApplicationCommand(id));

        assertThatThrownBy(() -> pay.register(new RegisterStudyFeePaymentCommand(id, new BigDecimal("50000"), "PSE-1")))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-03");
        assertThat(applications.findById(LoanApplicationId.of(id)).orElseThrow().getStatus())
                .isEqualTo(LoanApplicationStatus.SUBMITTED);
    }

    @Test
    void unknownApplicationIsNotFound() {
        UUID unknown = UUID.randomUUID();

        assertThatThrownBy(() -> attach.attach(new AttachRequirementCommand(unknown, "IDENTITY_DOCUMENT", "cc.pdf")))
                .isInstanceOf(LoanApplicationNotFoundException.class);
        assertThatThrownBy(() -> submit.submit(new SubmitLoanApplicationCommand(unknown)))
                .isInstanceOf(LoanApplicationNotFoundException.class);
        assertThatThrownBy(() -> pay.register(new RegisterStudyFeePaymentCommand(unknown, STUDY_FEE, "PSE-1")))
                .isInstanceOf(LoanApplicationNotFoundException.class);
    }
}
