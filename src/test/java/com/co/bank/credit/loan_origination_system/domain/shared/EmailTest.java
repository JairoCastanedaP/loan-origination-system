package com.co.bank.credit.loan_origination_system.domain.shared;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    void normalizesToLowerCaseWithoutSpaces() {
        assertThat(Email.of("  Maria.Perez@Banco.COM ").getValue()).isEqualTo("maria.perez@banco.com");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "maria", "maria@", "@banco.com", "maria@banco", "maria perez@banco.com"})
    void rejectsInvalidEmails(String value) {
        assertThatThrownBy(() -> Email.of(value)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void equalWhenSameNormalizedValue() {
        assertThat(Email.of("A@B.CO")).isEqualTo(Email.of("a@b.co"));
    }
}
