package com.co.bank.credit.loan_origination_system.creditproduct.domain;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireRange;

/**
 * Plazo de un crédito en meses.
 */
public final class Term implements Comparable<Term> {

    public static final int MIN_MONTHS = 1;
    public static final int MAX_MONTHS = 360;

    private final int months;

    private Term(int months) {
        this.months = months;
    }

    public static Term ofMonths(int months) {
        return new Term(requireRange(months, MIN_MONTHS, MAX_MONTHS, "Term in months"));
    }

    public int getMonths() {
        return months;
    }

    public boolean isBetween(Term min, Term max) {
        return compareTo(min) >= 0 && compareTo(max) <= 0;
    }

    @Override
    public int compareTo(Term other) {
        requireNonNull(other, "Term to compare cannot be null");
        return Integer.compare(months, other.months);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Term term)) return false;
        return months == term.months;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(months);
    }

    @Override
    public String toString() {
        return months + " months";
    }
}
