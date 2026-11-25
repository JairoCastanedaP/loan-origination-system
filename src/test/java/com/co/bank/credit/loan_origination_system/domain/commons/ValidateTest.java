package com.co.bank.credit.loan_origination_system.domain.commons;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ValidateTest {

    @Test
    void requireNonBlankTrims() {
        assertThat(Validate.requireNonBlank("  abc ", "msg")).isEqualTo("abc");
        assertThatThrownBy(() -> Validate.requireNonBlank("  ", "msg"))
                .isInstanceOf(ValueObjectException.class).hasMessage("msg");
    }

    @Test
    void requireLengthAndRange() {
        assertThat(Validate.requireLength("abc", 1, 3, "Field")).isEqualTo("abc");
        assertThatThrownBy(() -> Validate.requireLength("abcd", 1, 3, "Field"))
                .isInstanceOf(ValueObjectException.class);
        assertThat(Validate.requireRange(12, 12, 60, "Term")).isEqualTo(12);
        assertThatThrownBy(() -> Validate.requireRange(61, 12, 60, "Term"))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void requirePattern() {
        Pattern digits = Pattern.compile("\\d+");

        assertThat(Validate.requirePattern("123", digits, "msg")).isEqualTo("123");
        assertThatThrownBy(() -> Validate.requirePattern("12a", digits, "msg"))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void numericSignRules() {
        assertThat(Validate.requireNotNegative(BigDecimal.ZERO, "Amount")).isZero();
        assertThatThrownBy(() -> Validate.requireNotNegative(new BigDecimal("-0.01"), "Amount"))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> Validate.requirePositive(BigDecimal.ZERO, "Amount"))
                .isInstanceOf(ValueObjectException.class);
    }
}
