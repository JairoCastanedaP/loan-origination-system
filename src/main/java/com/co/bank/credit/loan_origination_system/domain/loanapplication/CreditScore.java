package com.co.bank.credit.loan_origination_system.domain.loanapplication;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireRange;

/**
 * Puntaje de la central de riesgo, entre 0 y 1000.
 */
public final class CreditScore {

    public static final int MIN = 0;
    public static final int MAX = 1000;

    private final int value;

    private CreditScore(int value) {
        this.value = value;
    }

    public static CreditScore of(int value) {
        return new CreditScore(requireRange(value, MIN, MAX, "Credit score"));
    }

    public boolean isAtLeast(int threshold) {
        return value >= threshold;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CreditScore that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
