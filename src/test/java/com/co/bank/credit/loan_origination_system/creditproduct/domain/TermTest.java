package com.co.bank.credit.loan_origination_system.creditproduct.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TermTest {

    @ParameterizedTest
    @ValueSource(ints = {Term.MIN_MONTHS, 12, 60, Term.MAX_MONTHS})
    void acceptsMonthsInsideTheRange(int months) {
        assertThat(Term.ofMonths(months).getMonths()).isEqualTo(months);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 361})
    void rejectsMonthsOutsideTheRange(int months) {
        assertThatThrownBy(() -> Term.ofMonths(months)).isInstanceOf(ValueObjectException.class);
    }

    @ParameterizedTest
    @CsvSource({"11,false", "12,true", "36,true", "60,true", "61,false"})
    void isBetweenIncludesBothLimits(int months, boolean expected) {
        assertThat(Term.ofMonths(months).isBetween(Term.ofMonths(12), Term.ofMonths(60))).isEqualTo(expected);
    }

    @Test
    void comparesAndEqualsByMonths() {
        assertThat(Term.ofMonths(12)).isEqualTo(Term.ofMonths(12)).hasSameHashCodeAs(Term.ofMonths(12));
        assertThat(Term.ofMonths(12)).isLessThan(Term.ofMonths(24));
        assertThatThrownBy(() -> Term.ofMonths(12).compareTo(null)).isInstanceOf(ValueObjectException.class);
    }
}
