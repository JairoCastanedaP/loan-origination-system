package com.co.bank.credit.loan_origination_system.survey.domain;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireRange;

/**
 * Calificación de la encuesta, de 1 (muy insatisfecho) a 5 (muy satisfecho). RN-13.
 */
public final class Rating {

    public static final int MIN = 1;
    public static final int MAX = 5;

    private final int value;

    private Rating(int value) {
        this.value = value;
    }

    public static Rating of(int value) {
        return new Rating(requireRange(value, MIN, MAX, "Rating"));
    }

    public boolean isPositive() {
        return value >= 4;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Rating that)) return false;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }

    @Override
    public String toString() {
        return value + "/" + MAX;
    }
}
