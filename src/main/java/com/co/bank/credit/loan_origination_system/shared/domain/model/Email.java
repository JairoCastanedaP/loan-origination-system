package com.co.bank.credit.loan_origination_system.shared.domain.model;

import java.util.Locale;
import java.util.regex.Pattern;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requirePattern;

/**
 * Correo electrónico normalizado en minúsculas.
 */
public final class Email {

    private static final int MAX_LENGTH = 254;
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$"
    );

    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        String normalized = requireNonBlank(value, "Email cannot be null or blank").toLowerCase(Locale.ROOT);
        requireLength(normalized, 3, MAX_LENGTH, "Email");
        requirePattern(normalized, EMAIL_PATTERN, "Invalid email format: " + value);
        return new Email(normalized);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Email email)) return false;
        return value.equals(email.value);
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
