package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifica el diagrama de estados del análisis de requerimientos (sección 6) contra el enum.
 * La tabla esperada se escribe aquí a mano, no se toma del enum, para que un cambio accidental
 * en {@link LoanApplicationStatus#allowedTransitions()} haga fallar la prueba.
 */
class LoanApplicationStatusTest {

    private static final Map<LoanApplicationStatus, Set<LoanApplicationStatus>> EXPECTED =
            new EnumMap<>(LoanApplicationStatus.class);

    static {
        EXPECTED.put(DRAFT, EnumSet.of(SUBMITTED, CANCELLED));
        EXPECTED.put(SUBMITTED, EnumSet.of(UNDER_REVIEW, CANCELLED));
        EXPECTED.put(UNDER_REVIEW, EnumSet.of(OBSERVED, IN_COMMITTEE, APPROVED, REJECTED));
        EXPECTED.put(OBSERVED, EnumSet.of(REMEDIATED, EXPIRED));
        EXPECTED.put(REMEDIATED, EnumSet.of(UNDER_REVIEW));
        EXPECTED.put(IN_COMMITTEE, EnumSet.of(APPROVED, REJECTED));
        EXPECTED.put(APPROVED, EnumSet.of(DISBURSED));
        EXPECTED.put(REJECTED, EnumSet.noneOf(LoanApplicationStatus.class));
        EXPECTED.put(DISBURSED, EnumSet.noneOf(LoanApplicationStatus.class));
        EXPECTED.put(EXPIRED, EnumSet.noneOf(LoanApplicationStatus.class));
        EXPECTED.put(CANCELLED, EnumSet.noneOf(LoanApplicationStatus.class));
    }

    static Stream<Arguments> allowedTransitions() {
        return EXPECTED.entrySet().stream()
                .flatMap(e -> e.getValue().stream().map(to -> Arguments.of(e.getKey(), to)));
    }

    static Stream<Arguments> forbiddenTransitions() {
        return Arrays.stream(values()).flatMap(from -> Arrays.stream(values())
                .filter(to -> !EXPECTED.get(from).contains(to))
                .map(to -> Arguments.of(from, to)));
    }

    @Test
    void everyStatusIsInTheExpectedTable() {
        assertThat(EXPECTED.keySet()).containsExactlyInAnyOrder(values());
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("allowedTransitions")
    void allowsTransitionsOfTheDiagram(LoanApplicationStatus from, LoanApplicationStatus to) {
        assertThat(from.canTransitionTo(to)).isTrue();
        assertThat(from.transitionTo(to)).isEqualTo(to);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("forbiddenTransitions")
    void rejectsEveryOtherTransition(LoanApplicationStatus from, LoanApplicationStatus to) {
        assertThat(from.canTransitionTo(to)).isFalse();
        assertThatThrownBy(() -> from.transitionTo(to))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("INVALID_TRANSITION");
    }

    @ParameterizedTest
    @EnumSource(value = LoanApplicationStatus.class, names = {"REJECTED", "DISBURSED", "EXPIRED", "CANCELLED"})
    void finalStatusesAreTerminal(LoanApplicationStatus status) {
        assertThat(status.isTerminal()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = LoanApplicationStatus.class, mode = EnumSource.Mode.EXCLUDE,
            names = {"REJECTED", "DISBURSED", "EXPIRED", "CANCELLED"})
    void otherStatusesAreNotTerminal(LoanApplicationStatus status) {
        assertThat(status.isTerminal()).isFalse();
    }

    @Test
    void nullTargetIsRejected() {
        assertThat(DRAFT.canTransitionTo(null)).isFalse();
        assertThatThrownBy(() -> DRAFT.transitionTo(null)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void parsesIgnoringCaseAndSpaces() {
        assertThat(LoanApplicationStatus.of(" under_review ")).isEqualTo(UNDER_REVIEW);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "PENDING"})
    void rejectsUnknownStatus(String value) {
        assertThatThrownBy(() -> LoanApplicationStatus.of(value)).isInstanceOf(ValueObjectException.class);
    }
}
