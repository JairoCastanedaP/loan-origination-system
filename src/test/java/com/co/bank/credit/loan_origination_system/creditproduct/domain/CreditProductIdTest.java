package com.co.bank.credit.loan_origination_system.creditproduct.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditProductIdTest {

    @Test
    void parsesAndComparesByValue() {
        UUID uuid = UUID.randomUUID();

        assertThat(CreditProductId.of(" " + uuid + " ")).isEqualTo(CreditProductId.of(uuid));
        assertThat(CreditProductId.generate()).isNotEqualTo(CreditProductId.generate());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "not-a-uuid", "00000000-0000-0000-0000-000000000000"})
    void rejectsInvalidValues(String value) {
        assertThatThrownBy(() -> CreditProductId.of(value)).isInstanceOf(ValueObjectException.class);
    }
}
