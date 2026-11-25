package com.co.bank.credit.loan_origination_system.domain.shared;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Tipos de documento de identidad aceptados, con el formato válido de cada uno.
 */
public enum DocumentType {

    CC("Cédula de ciudadanía", "^\\d{6,10}$"),
    CE("Cédula de extranjería", "^[A-Z0-9]{6,10}$"),
    PA("Pasaporte", "^[A-Z0-9]{6,12}$");

    private final String description;
    private final Pattern pattern;

    DocumentType(String description, String regex) {
        this.description = description;
        this.pattern = Pattern.compile(regex);
    }

    public static DocumentType of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("Document type cannot be null or blank");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid document type: " + value);
        }
    }

    public boolean isValidNumber(String number) {
        return number != null && pattern.matcher(number).matches();
    }

    public String getDescription() {
        return description;
    }
}
