package com.co.bank.credit.loan_origination_system.domain.shared;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PersonNameTest {

    @Test
    void acceptsAccentsEnyeAndCompoundNames() {
        assertThat(FirstName.of("  José   María ").getValue()).isEqualTo("José María");
        assertThat(FirstName.of("Ana").getValue()).isEqualTo("Ana");
        assertThat(LastName.of("Castañeda-Pedraza").getValue()).isEqualTo("Castañeda-Pedraza");
        assertThat(LastName.of("D'Angelo").getValue()).isEqualTo("D'Angelo");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"A", "Juan2", "Juan_Pablo", "-Juan", "Juan--Pablo"})
    void rejectsInvalidNames(String value) {
        assertThatThrownBy(() -> FirstName.of(value)).isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> LastName.of(value)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void rejectsNamesLongerThanSixtyCharacters() {
        assertThatThrownBy(() -> LastName.of("a".repeat(61))).isInstanceOf(ValueObjectException.class);
        assertThat(LastName.of("a".repeat(60)).getValue()).hasSize(60);
    }
}
