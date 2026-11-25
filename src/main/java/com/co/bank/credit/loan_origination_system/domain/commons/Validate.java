package com.co.bank.credit.loan_origination_system.domain.commons;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Validaciones reutilizables del dominio. Todas lanzan {@link ValueObjectException}.
 */
public final class Validate {

    private Validate() {
    }

    public static <T> T requireNonNull(T value, String message) {
        if (value == null) {
            throw new ValueObjectException(message);
        }
        return value;
    }

    /**
     * Exige un texto no vacío y lo devuelve sin espacios al inicio ni al final.
     */
    public static String requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException(message);
        }
        return value.trim();
    }

    public static String requireLength(String value, int min, int max, String fieldName) {
        requireNonNull(value, fieldName + " cannot be null");
        if (value.length() < min || value.length() > max) {
            throw new ValueObjectException(
                    fieldName + " must be between " + min + " and " + max + " characters");
        }
        return value;
    }

    public static String requirePattern(String value, Pattern pattern, String message) {
        requireNonNull(value, message);
        if (!pattern.matcher(value).matches()) {
            throw new ValueObjectException(message);
        }
        return value;
    }

    public static int requireRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new ValueObjectException(
                    fieldName + " must be between " + min + " and " + max + ": " + value);
        }
        return value;
    }

    public static BigDecimal requireNotNegative(BigDecimal value, String fieldName) {
        requireNonNull(value, fieldName + " cannot be null");
        if (value.signum() < 0) {
            throw new ValueObjectException(fieldName + " cannot be negative: " + value);
        }
        return value;
    }

    public static BigDecimal requirePositive(BigDecimal value, String fieldName) {
        requireNonNull(value, fieldName + " cannot be null");
        if (value.signum() <= 0) {
            throw new ValueObjectException(fieldName + " must be greater than zero: " + value);
        }
        return value;
    }
}
