package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplicationNumberTest {

    @Test
    void buildsFromYearAndSequence() {
        ApplicationNumber number = ApplicationNumber.of(2026, 42);

        assertThat(number.getValue()).isEqualTo("LOS-2026-000042");
        assertThat(number.getYear()).isEqualTo(2026);
        assertThat(number.getSequence()).isEqualTo(42);
        assertThat(ApplicationNumber.of(2026, 999_999).getValue()).isEqualTo("LOS-2026-999999");
    }

    @Test
    void parsesAndNormalizesText() {
        assertThat(ApplicationNumber.of(" los-2026-000042 ")).isEqualTo(ApplicationNumber.of(2026, 42));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"LOS-26-000001", "LOS-2026-1", "LOS-2026-0000001", "ABC-2026-000001", "LOS2026000001"})
    void rejectsInvalidText(String value) {
        assertThatThrownBy(() -> ApplicationNumber.of(value)).isInstanceOf(ValueObjectException.class);
    }

    @ParameterizedTest
    @CsvSource({"1999,1", "10000,1", "2026,0", "2026,1000000"})
    void rejectsYearOrSequenceOutOfRange(int year, int sequence) {
        assertThatThrownBy(() -> ApplicationNumber.of(year, sequence)).isInstanceOf(ValueObjectException.class);
    }
}
