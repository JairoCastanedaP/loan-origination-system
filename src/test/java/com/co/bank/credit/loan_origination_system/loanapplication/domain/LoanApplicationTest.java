package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationApproved;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationDisbursed;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationObserved;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationStatusChanged;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.event.LoanApplicationSubmitted;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoanApplicationTest {

    @Test
    void happyPathFromDraftToDisbursed() {
        LoanApplication application = underReview("10000000", 12);

        RiskAssessment assessment = application.assessRisk(CreditScore.of(720), HIGH_INCOME, POLICY, NOW);
        application.approve("analyst-01", null, POLICY, NOW);
        application.disburse("ops-01", NOW);

        assertThat(assessment.recommendation()).isEqualTo(Recommendation.APPROVE);
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.DISBURSED);
        assertThat(application.getPaymentPlan()).get()
                .satisfies(plan -> assertThat(plan.totalPrincipal()).isEqualTo(Money.of("10000000")));
        assertThat(application.getDecision()).get()
                .satisfies(d -> assertThat(d.followsRecommendation()).isTrue());

        List<DomainEvent> events = application.pullDomainEvents();
        assertThat(events).hasAtLeastOneElementOfType(LoanApplicationSubmitted.class)
                .hasAtLeastOneElementOfType(LoanApplicationApproved.class)
                .hasAtLeastOneElementOfType(LoanApplicationDisbursed.class);
        assertThat(events).filteredOn(LoanApplicationStatusChanged.class::isInstance)
                .extracting(e -> ((LoanApplicationStatusChanged) e).to())
                .containsExactly(LoanApplicationStatus.SUBMITTED, LoanApplicationStatus.UNDER_REVIEW,
                        LoanApplicationStatus.APPROVED, LoanApplicationStatus.DISBURSED);
    }

    @Test
    void rn01AmountAndTermMustFitTheProduct() {
        assertThatThrownBy(() -> draft("500000", 12)).isInstanceOf(BusinessRuleException.class)
                .extracting("ruleCode").isEqualTo("RN-01");
        assertThatThrownBy(() -> draft("10000000", 72)).isInstanceOf(BusinessRuleException.class)
                .extracting("ruleCode").isEqualTo("RN-01");
    }

    @Test
    void rn02CannotSubmitWithMissingRequirements() {
        LoanApplication application = draft("10000000", 12);
        application.attachRequirement(RequirementType.IDENTITY_DOCUMENT, "cc.pdf", NOW);

        assertThatThrownBy(() -> application.submit(NOW)).isInstanceOf(BusinessRuleException.class)
                .extracting("ruleCode").isEqualTo("RN-02");
        assertThat(application.missingRequirements()).containsExactly(RequirementType.EMPLOYMENT_CERTIFICATE);
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.DRAFT);
    }

    @Test
    void rn03StudyFeeMustMatch() {
        LoanApplication application = draft("10000000", 12);
        application.attachRequirement(RequirementType.IDENTITY_DOCUMENT, "cc.pdf", NOW);
        application.attachRequirement(RequirementType.EMPLOYMENT_CERTIFICATE, "cert.pdf", NOW);
        application.submit(NOW);

        assertThatThrownBy(() -> application.registerStudyFeePayment(Money.of("50000"), "PAY-1", NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-03");
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.SUBMITTED);
    }

    @Test
    void rn06RecommendsRejectionWithEachReason() {
        LoanApplication application = underReview("10000000", 12);

        RiskAssessment assessment = application.assessRisk(CreditScore.of(450), LOW_INCOME, POLICY, NOW);

        assertThat(assessment.recommendation()).isEqualTo(Recommendation.REJECT);
        assertThat(assessment.reasons()).containsExactly(RecommendationReason.INSUFFICIENT_SCORE,
                RecommendationReason.INSUFFICIENT_PAYMENT_CAPACITY);
        assertThat(assessment.paymentCapacityRatio()).isEqualByComparingTo("0.4673");
    }

    @Test
    void rn09aApprovingAgainstRecommendationRequiresJustification() {
        LoanApplication application = underReview("10000000", 12);
        application.assessRisk(CreditScore.of(450), HIGH_INCOME, POLICY, NOW);

        assertThatThrownBy(() -> application.approve("analyst-01", " ", POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-09a");

        application.approve("analyst-01", "Cliente con historial interno excelente", POLICY, NOW);
        assertThat(application.getDecision()).get()
                .satisfies(d -> assertThat(d.followsRecommendation()).isFalse());
    }

    @Test
    void rn09RejectionRequiresReason() {
        LoanApplication application = underReview("10000000", 12);
        application.assessRisk(CreditScore.of(450), HIGH_INCOME, POLICY, NOW);

        assertThatThrownBy(() -> application.reject("analyst-01", "", POLICY, NOW))
                .hasMessageContaining("RN-09");
        application.reject("analyst-01", "Score insuficiente", POLICY, NOW);
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.REJECTED);
    }

    @Test
    void cannotDecideWithoutRiskAssessment() {
        LoanApplication application = underReview("10000000", 12);

        assertThatThrownBy(() -> application.approve("analyst-01", null, POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RISK_NOT_ASSESSED");
    }

    @Test
    void rn07HighAmountsRequireCommittee() {
        LoanApplication application = underReview("60000000", 60);
        application.assessRisk(CreditScore.of(800), Money.of("20000000"), POLICY, NOW);

        assertThatThrownBy(() -> application.approve("analyst-01", null, POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-07");

        application.escalateToCommittee(POLICY, NOW);
        application.approve("committee", null, POLICY, NOW);

        assertThat(application.getDecision()).get().satisfies(d -> assertThat(d.byCommittee()).isTrue());
    }

    @Test
    void rn07LowAmountsCannotBeEscalated() {
        LoanApplication application = underReview("10000000", 12);
        application.assessRisk(CreditScore.of(800), HIGH_INCOME, POLICY, NOW);

        assertThatThrownBy(() -> application.escalateToCommittee(POLICY, NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-07");
    }

    @Test
    void observeRemediateAndResumeReview() {
        LoanApplication application = underReview("10000000", 12);
        application.assessRisk(CreditScore.of(720), HIGH_INCOME, POLICY, NOW);

        application.observe("El certificado laboral tiene más de 30 días", POLICY, NOW);
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.OBSERVED);
        assertThat(application.pullDomainEvents()).filteredOn(LoanApplicationObserved.class::isInstance)
                .singleElement()
                .satisfies(e -> assertThat(((LoanApplicationObserved) e).remediationDueAt())
                        .isEqualTo(NOW.plus(Duration.ofDays(5))));

        application.attachRequirement(RequirementType.EMPLOYMENT_CERTIFICATE, "cert-nuevo.pdf", NOW.plusSeconds(60));
        application.remediate(NOW.plus(Duration.ofDays(2)));
        application.resumeReview(NOW.plus(Duration.ofDays(2)));

        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.UNDER_REVIEW);
        assertThat(application.getObservations()).allSatisfy(o -> assertThat(o.isPending()).isFalse());
        assertThat(application.getRiskAssessment()).isEmpty();
    }

    @Test
    void rn08ExpiresOnlyAfterDeadline() {
        LoanApplication application = underReview("10000000", 12);
        application.observe("Falta el extracto bancario", POLICY, NOW);

        assertThatThrownBy(() -> application.expire(NOW.plus(Duration.ofDays(4))))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-08");
        assertThatThrownBy(() -> application.remediate(NOW.plus(Duration.ofDays(6))))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-08");

        application.expire(NOW.plus(Duration.ofDays(6)));
        assertThat(application.getStatus()).isEqualTo(LoanApplicationStatus.EXPIRED);
    }

    @Test
    void rn11CannotCancelOnceUnderReview() {
        LoanApplication application = underReview("10000000", 12);

        assertThatThrownBy(() -> application.cancel("Ya no lo necesito", NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("INVALID_TRANSITION");

        LoanApplication draft = draft("10000000", 12);
        draft.cancel("Ya no lo necesito", NOW);
        assertThat(draft.getStatus()).isEqualTo(LoanApplicationStatus.CANCELLED);
    }

    @Test
    void requirementsAreLockedAfterSubmission() {
        LoanApplication application = underReview("10000000", 12);

        assertThatThrownBy(() -> application.attachRequirement(RequirementType.IDENTITY_DOCUMENT, "x.pdf", NOW))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("REQUIREMENTS_LOCKED");
    }

    @Test
    void snapshotsProductConditionsAtCreation() {
        LoanApplication application = draft("10000000", 12);

        assertThat(application.getInterestRate().getAnnualPercentage()).isEqualByComparingTo("24");
        assertThat(application.getStudyFee()).isEqualTo(STUDY_FEE);
        assertThat(application.getNumber().getValue()).isEqualTo("LOS-2026-000001");
    }
}
