package com.co.bank.credit.loan_origination_system.domain.shared;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNotNegative;

/**
 * Cantidad de dinero en una moneda. Siempre con 2 decimales y redondeo bancario
 * (HALF_EVEN). Nunca es negativa: un saldo puede llegar a cero, pero no por debajo.
 */
public final class Money implements Comparable<Money> {

    public static final int SCALE = 2;
    public static final RoundingMode ROUNDING = RoundingMode.HALF_EVEN;
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("COP");

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public static Money of(BigDecimal amount, Currency currency) {
        requireNotNegative(amount, "Money amount");
        requireNonNull(currency, "Money currency cannot be null");
        return new Money(amount.setScale(SCALE, ROUNDING), currency);
    }

    public static Money of(BigDecimal amount) {
        return of(amount, DEFAULT_CURRENCY);
    }

    public static Money of(String amount) {
        requireNonNull(amount, "Money amount cannot be null");
        try {
            return of(new BigDecimal(amount.trim()));
        } catch (NumberFormatException e) {
            throw new ValueObjectException("Invalid money amount: " + amount);
        }
    }

    public static Money zero() {
        return of(BigDecimal.ZERO);
    }

    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        requireSameCurrency(other);
        BigDecimal result = amount.subtract(other.amount);
        if (result.signum() < 0) {
            throw new ValueObjectException("Money cannot be negative: " + amount + " - " + other.amount);
        }
        return new Money(result, currency);
    }

    public Money multiply(BigDecimal factor) {
        requireNotNegative(factor, "Money factor");
        return new Money(amount.multiply(factor).setScale(SCALE, ROUNDING), currency);
    }

    public boolean isZero() {
        return amount.signum() == 0;
    }

    public boolean isGreaterThan(Money other) {
        return compareTo(other) > 0;
    }

    public boolean isLessThan(Money other) {
        return compareTo(other) < 0;
    }

    public boolean isBetween(Money min, Money max) {
        return compareTo(min) >= 0 && compareTo(max) <= 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    @Override
    public int compareTo(Money other) {
        requireSameCurrency(other);
        return amount.compareTo(other.amount);
    }

    private void requireSameCurrency(Money other) {
        requireNonNull(other, "Money to operate cannot be null");
        if (!currency.equals(other.currency)) {
            throw new ValueObjectException(
                    "Currency mismatch: " + currency.getCurrencyCode() + " vs " + other.currency.getCurrencyCode());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Money money)) return false;
        return amount.equals(money.amount) && currency.equals(money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getCurrencyCode() + " " + amount.toPlainString();
    }
}
