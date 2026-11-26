package com.co.bank.credit.loan_origination_system.shared.domain.model;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneNumberTest {

    @ParameterizedTest
    @CsvSource({
            "3101234567, 3101234567, true",
            "'+57 310 123 4567', 3101234567, true",
            "573101234567, 3101234567, true",
            "(608) 742-1234, 6087421234, false",
            "601.234.5678, 6012345678, false"
    })
    void normalizesValidNumbers(String input, String expected, boolean mobile) {
        PhoneNumber phone = PhoneNumber.of(input);

        assertThat(phone.getValue()).isEqualTo(expected);
        assertThat(phone.isMobile()).isEqualTo(mobile);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"310123456", "31012345678", "2101234567", "7421234", "31O1234567"})
    void rejectsInvalidNumbers(String value) {
        assertThatThrownBy(() -> PhoneNumber.of(value)).isInstanceOf(ValueObjectException.class);
    }
}
