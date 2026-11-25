package com.co.bank.credit.loan_origination_system.domain.shared;

import java.util.regex.Pattern;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requirePattern;

/**
 * Reglas comunes de {@link FirstName} y {@link LastName}: solo letras (con tildes y ñ),
 * separadas por un espacio, apóstrofo o guion; entre 2 y 60 caracteres.
 */
final class PersonNameRules {

    static final int MIN_LENGTH = 2;
    static final int MAX_LENGTH = 60;
    private static final Pattern NAME_PATTERN = Pattern.compile("^\\p{L}+(?:[ '-]\\p{L}+)*$");
    private static final Pattern MULTIPLE_SPACES = Pattern.compile("\\s+");

    private PersonNameRules() {
    }

    static String normalize(String value, String fieldName) {
        String normalized = MULTIPLE_SPACES
                .matcher(requireNonBlank(value, fieldName + " cannot be null or blank"))
                .replaceAll(" ");
        requireLength(normalized, MIN_LENGTH, MAX_LENGTH, fieldName);
        requirePattern(normalized, NAME_PATTERN, fieldName + " contains invalid characters: " + value);
        return normalized;
    }
}
