package com.co.bank.credit.loan_origination_system.shared.domain.model;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void roundsToTwoDecimalsWithBankersRounding() {
        assertThat(Money.of("10.125").getAmount()).isEqualByComparingTo("10.12");
        assertThat(Money.of("10.135").getAmount()).isEqualByComparingTo("10.14");
        assertThat(Money.of("100").getAmount().scale()).isEqualTo(2);
    }

    @Test
    void usesColombianPesoByDefault() {
        assertThat(Money.of("1000").getCurrency().getCurrencyCode()).isEqualTo("COP");
    }

    @Test
    void rejectsNegativeNullAndInvalidAmounts() {
        assertThatThrownBy(() -> Money.of("-1")).isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> Money.of((BigDecimal) null)).isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> Money.of("abc")).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void addsSubtractsAndMultiplies() {
        Money a = Money.of("1000.50");
        Money b = Money.of("200.25");

        assertThat(a.add(b)).isEqualTo(Money.of("1200.75"));
        assertThat(a.subtract(b)).isEqualTo(Money.of("800.25"));
        assertThat(a.multiply(new BigDecimal("0.40"))).isEqualTo(Money.of("400.20"));
        assertThat(a.subtract(a).isZero()).isTrue();
    }

    @Test
    void subtractionCannotGoBelowZero() {
        assertThatThrownBy(() -> Money.of("100").subtract(Money.of("100.01")))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void comparesAmounts() {
        Money min = Money.of("1000000");
        Money max = Money.of("50000000");

        assertThat(Money.of("50000000.01").isGreaterThan(max)).isTrue();
        assertThat(Money.of("999999.99").isLessThan(min)).isTrue();
        assertThat(Money.of("1000000").isBetween(min, max)).isTrue();
    }

    @Test
    void rejectsOperationsBetweenDifferentCurrencies() {
        Money usd = Money.of(BigDecimal.TEN, Currency.getInstance("USD"));

        assertThatThrownBy(() -> Money.of("10").add(usd)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void equalityIgnoresInputScale() {
        assertThat(Money.of("10")).isEqualTo(Money.of("10.00"));
        assertThat(Money.of("10")).hasSameHashCodeAs(Money.of("10.0"));
    }
}
