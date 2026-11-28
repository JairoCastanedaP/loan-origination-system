package com.co.bank.credit.loan_origination_system.shared.application.validation;

import com.co.bank.credit.loan_origination_system.shared.application.exception.InvalidCommandException;

/**
 * Validaciones de presencia para los Commands y Queries. El formato de cada dato lo valida su Value Object.
 */
public final class Require {

    private Require() {
    }

    public static <T> T nonNull(T value, String field) {
        if (value == null) {
            throw new InvalidCommandException(field + " is required");
        }
        return value;
    }

    public static String nonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new InvalidCommandException(field + " is required");
        }
        return value.strip();
    }

    public static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
