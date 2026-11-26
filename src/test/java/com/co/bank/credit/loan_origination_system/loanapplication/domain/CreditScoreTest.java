package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditScoreTest {

    @ParameterizedTest
    @ValueSource(ints = {CreditScore.MIN, 500, CreditScore.MAX})
    void acceptsScoresInsideTheRange(int value) {
        assertThat(CreditScore.of(value).getValue()).isEqualTo(value);
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 1001})
    void rejectsScoresOutsideTheRange(int value) {
        assertThatThrownBy(() -> CreditScore.of(value)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void isAtLeastIncludesTheThreshold() {
        assertThat(CreditScore.of(500).isAtLeast(500)).isTrue();
        assertThat(CreditScore.of(499).isAtLeast(500)).isFalse();
    }
}
