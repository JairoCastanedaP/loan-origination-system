package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationStatusChanged;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Casos límite de la solicitud: plazos exactos, umbral del comité y acciones en estados no permitidos.
 */
class LoanApplicationEdgeCasesTest {

    private static final Instant DEADLINE = NOW.plus(Duration.ofDays(5));

    private static LoanApplication observed() {
        LoanApplication application = underReview("10000000", 12);
        application.observe("Falta el extracto bancario", POLICY, NOW);
        return application;
    }

    @Test
    void rn08CanRemediateExactlyOnTheDeadline() {
        LoanApplication application = observed();

        application.remediate(DEADLINE);

        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.REMEDIATED);
    }

    @Test
    void rn08CannotExpireExactlyOnTheDeadline() {
        LoanApplication application = observed();

        assertThat(application.isRemediationOverdue(DEADLINE)).isFalse();
        assertThatThrownBy(() -> application.expire(DEADLINE))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-08");
    }

    @Test
    void rn08ExpiresOneSecondAfterTheDeadline() {
        LoanApplication application = observed();
        Instant justAfter = DEADLINE.plusSeconds(1);

        assertThatThrownBy(() -> application.remediate(justAfter))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-08");

        application.expire(justAfter);
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.EXPIRED);
    }

    @Test
    void rn07AmountEqualToTheThresholdIsDecidedByTheAnalyst() {
        LoanApplication application = underReview("50000000", 60);
        application.assessRisk(CreditScore.of(720), HIGH_INCOME, POLICY, NOW);

        assertThatThrownBy(() -> application.escalateToCommittee(POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-07");

        application.approve("analyst-01", null, POLICY, NOW);
        assertThat(application.getDecision()).get().satisfies(d -> assertThat(d.byCommittee()).isFalse());
    }

    @Test
    void rn07OneCentAboveTheThresholdGoesToCommittee() {
        LoanApplication application = underReview("50000000.01", 60);
        application.assessRisk(CreditScore.of(720), HIGH_INCOME, POLICY, NOW);

        assertThatThrownBy(() -> application.approve("analyst-01", null, POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-07");

        application.escalateToCommittee(POLICY, NOW);
        application.approve("committee", null, POLICY, NOW);
        assertThat(application.getDecision()).get().satisfies(d -> assertThat(d.byCommittee()).isTrue());
    }

    @Test
    void rn01AcceptsTheProductLimits() {
        assertThat(draft("1000000", 12).getStatus()).isEqualTo(LoanApplicationStatus.DRAFT);
        assertThat(draft("100000000", 60).getStatus()).isEqualTo(LoanApplicationStatus.DRAFT);
        assertThatThrownBy(() -> draft("999999.99", 12))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-01");
    }

    @Test
    void cannotApplyToAnInactiveProduct() {
        CreditProduct product = personalLoan();
        product.deactivate();

        assertThatThrownBy(() -> LoanApplication.create(LoanApplicationId.generate(), ApplicationNumber.of(2026, 1),
                CustomerId.generate(), product, Money.of("10000000"), Term.ofMonths(12), NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("PRODUCT_INACTIVE");
    }

    @Test
    void rn02OnlyAcceptsRequirementsOfTheProduct() {
        LoanApplication application = draft("10000000", 12);

        assertThatThrownBy(() -> application.attachRequirement(RequirementType.VEHICLE_QUOTE, "cotizacion.pdf", NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-02");
        assertThat(application.missingRequirements())
                .containsExactlyInAnyOrder(RequirementType.IDENTITY_DOCUMENT, RequirementType.EMPLOYMENT_CERTIFICATE);
    }

    @Test
    void rn11CanCancelWhileSubmitted() {
        LoanApplication application = draft("10000000", 12);
        application.attachRequirement(RequirementType.IDENTITY_DOCUMENT, "cc.pdf", NOW);
        application.attachRequirement(RequirementType.EMPLOYMENT_CERTIFICATE, "cert.pdf", NOW);
        application.submit(NOW);

        application.cancel("Encontré otra oferta", NOW);

        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.CANCELLED);
        assertThat(application.getCancellationReason()).contains("Encontré otra oferta");
    }

    @Test
    void cannotRegisterThePaymentTwice() {
        LoanApplication application = underReview("10000000", 12);

        assertThatThrownBy(() -> application.registerStudyFeePayment(STUDY_FEE, "PAY-002", NOW))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cannotDisburseBeforeApproval() {
        LoanApplication application = underReview("10000000", 12);

        assertThatThrownBy(() -> application.disburse("ops-01", NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("INVALID_TRANSITION");
    }

    @Test
    void finalDecisionCannotBeChanged() {
        LoanApplication application = underReview("10000000", 12);
        application.assessRisk(CreditScore.of(720), HIGH_INCOME, POLICY, NOW);
        application.reject("analyst-01", "Política interna", POLICY, NOW);

        assertThatThrownBy(() -> application.approve("analyst-02", "Reconsiderar", POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void cannotAssessRiskOutsideReview() {
        LoanApplication application = draft("10000000", 12);

        assertThatThrownBy(() -> application.assessRisk(CreditScore.of(720), HIGH_INCOME, POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void everyTransitionEmitsAStatusChangedEvent() {
        LoanApplication application = underReview("10000000", 12);
        application.assessRisk(CreditScore.of(720), HIGH_INCOME, POLICY, NOW);
        application.approve("analyst-01", null, POLICY, NOW);
        application.disburse("ops-01", NOW);

        assertThat(application.pullDomainEvents())
                .filteredOn(LoanApplicationStatusChanged.class::isInstance)
                .map(e -> ((LoanApplicationStatusChanged) e).to())
                .containsExactly(LoanApplicationStatus.SUBMITTED, LoanApplicationStatus.UNDER_REVIEW,
                        LoanApplicationStatus.APPROVED, LoanApplicationStatus.DISBURSED);
        assertThat(application.hasDomainEvents()).isFalse();
    }
}
