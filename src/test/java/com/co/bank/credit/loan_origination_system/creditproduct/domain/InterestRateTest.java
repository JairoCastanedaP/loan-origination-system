package com.co.bank.credit.loan_origination_system.creditproduct.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class InterestRateTest {

    @Test
    void normalizesToFourDecimals() {
        InterestRate rate = InterestRate.ofAnnualPercentage("24");

        assertThat(rate.getAnnualPercentage()).isEqualByComparingTo("24").hasScaleOf(4);
        assertThat(rate).isEqualTo(InterestRate.ofAnnualPercentage(new BigDecimal("24.00")));
        assertThat(rate.toString()).isEqualTo("24% E.A.");
    }

    @Test
    void convertsEffectiveAnnualRateToMonthlyEquivalent() {
        InterestRate rate = InterestRate.ofAnnualPercentage("24");

        assertThat(rate.annualFraction()).isEqualByComparingTo("0.24");
        assertThat(rate.monthlyFraction()).isCloseTo(new BigDecimal("0.01808758248351072"), within(new BigDecimal("1e-15")));
    }

    @Test
    void compoundingTwelveMonthlyRatesGivesBackTheAnnualRate() {
        InterestRate rate = InterestRate.ofAnnualPercentage("18.5");

        double compounded = Math.pow(1 + rate.monthlyFraction().doubleValue(), 12) - 1;

        assertThat(compounded).isCloseTo(0.185, within(1e-12));
    }

    @ParameterizedTest
    @ValueSource(strings = {"0.0001", "1", "100"})
    void acceptsRatesInsideTheRange(String value) {
        assertThat(InterestRate.ofAnnualPercentage(value).getAnnualPercentage()).isEqualByComparingTo(value);
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "100.0001", "150", "abc", "", "  "})
    void rejectsInvalidRates(String value) {
        assertThatThrownBy(() -> InterestRate.ofAnnualPercentage(value)).isInstanceOf(ValueObjectException.class);
    }

    @ParameterizedTest
    @NullSource
    void rejectsNull(String value) {
        assertThatThrownBy(() -> InterestRate.ofAnnualPercentage(value)).isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> InterestRate.ofAnnualPercentage((BigDecimal) null)).isInstanceOf(ValueObjectException.class);
    }
}
