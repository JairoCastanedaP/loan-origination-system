package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import java.util.regex.Pattern;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requirePattern;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireRange;

/**
 * Número legible de la solicitud: LOS-AAAA-NNNNNN (por ejemplo LOS-2026-000123).
 */
public final class ApplicationNumber {

    private static final String PREFIX = "LOS";
    private static final Pattern PATTERN = Pattern.compile("^LOS-\\d{4}-\\d{6}$");
    private static final int MAX_SEQUENCE = 999_999;

    private final String value;

    private ApplicationNumber(String value) {
        this.value = value;
    }

    public static ApplicationNumber of(String value) {
        String normalized = requireNonBlank(value, "Application number cannot be null or blank").toUpperCase();
        requirePattern(normalized, PATTERN, "Invalid application number: " + value);
        return new ApplicationNumber(normalized);
    }

    public static ApplicationNumber of(int year, int sequence) {
        requireRange(year, 2000, 9999, "Application year");
        requireRange(sequence, 1, MAX_SEQUENCE, "Application sequence");
        return new ApplicationNumber("%s-%d-%06d".formatted(PREFIX, year, sequence));
    }

    public int getYear() {
        return Integer.parseInt(value.substring(4, 8));
    }

    public int getSequence() {
        return Integer.parseInt(value.substring(9));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ApplicationNumber that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
