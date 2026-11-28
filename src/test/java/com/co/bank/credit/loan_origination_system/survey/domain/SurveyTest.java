package com.co.bank.credit.loan_origination_system.survey.domain;

import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationStatus;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.survey.domain.event.SurveyAnswered;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyTest {

    private static final Instant NOW = Instant.parse("2026-11-27T15:00:00Z");

    private static Survey answer(LoanApplicationStatus status, int rating, String comment) {
        return Survey.answer(SurveyId.generate(), LoanApplicationId.generate(), CustomerId.generate(),
                status, Rating.of(rating), comment, NOW);
    }

    @ParameterizedTest
    @EnumSource(value = LoanApplicationStatus.class, names = {"DISBURSED", "REJECTED", "EXPIRED", "CANCELLED"})
    void rn13CanAnswerClosedApplications(LoanApplicationStatus status) {
        Survey survey = answer(status, 5, "Muy ágil");

        assertThat(survey.getRating().getValue()).isEqualTo(5);
        assertThat(survey.getComment()).contains("Muy ágil");
        assertThat(survey.pullDomainEvents()).singleElement().isInstanceOf(SurveyAnswered.class);
    }

    @ParameterizedTest
    @EnumSource(value = LoanApplicationStatus.class, mode = EnumSource.Mode.EXCLUDE,
            names = {"DISBURSED", "REJECTED", "EXPIRED", "CANCELLED"})
    void rn13CannotAnswerOpenApplications(LoanApplicationStatus status) {
        assertThatThrownBy(() -> answer(status, 4, null))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-13");
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1})
    void rn13RatingGoesFromOneToFive(int value) {
        assertThatThrownBy(() -> Rating.of(value)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void ratingOfFourOrMoreIsPositive() {
        assertThat(Rating.of(4).isPositive()).isTrue();
        assertThat(Rating.of(3).isPositive()).isFalse();
    }

    @Test
    void commentIsOptionalAndLimited() {
        assertThat(answer(LoanApplicationStatus.DISBURSED, 3, "   ").getComment()).isEmpty();
        assertThatThrownBy(() -> answer(LoanApplicationStatus.DISBURSED, 3, "x".repeat(501)))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void reconstituteDoesNotEmitEvents() {
        Survey survey = Survey.reconstitute(SurveyId.generate(), LoanApplicationId.generate(), CustomerId.generate(),
                LoanApplicationStatus.REJECTED, Rating.of(2), null, NOW);

        assertThat(survey.hasDomainEvents()).isFalse();
    }
}
