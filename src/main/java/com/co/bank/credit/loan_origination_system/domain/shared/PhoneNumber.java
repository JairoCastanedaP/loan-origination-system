package com.co.bank.credit.loan_origination_system.domain.shared;

import java.util.regex.Pattern;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requirePattern;

/**
 * Número telefónico colombiano de 10 dígitos: celular (3XX XXX XXXX)
 * o fijo con indicativo (60X XXX XXXX). Acepta el prefijo +57 y separadores
 * (espacios, guiones, puntos, paréntesis), que se eliminan al normalizar.
 */
public final class PhoneNumber {

    private static final String COUNTRY_CODE = "57";
    private static final Pattern SEPARATORS = Pattern.compile("[\\s().-]");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(3\\d{9}|60\\d{8})$");

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    public static PhoneNumber of(String value) {
        String normalized = SEPARATORS.matcher(requireNonBlank(value, "Phone number cannot be null or blank"))
                .replaceAll("");
        if (normalized.startsWith("+")) {
            normalized = normalized.substring(1);
        }
        if (normalized.length() == 12 && normalized.startsWith(COUNTRY_CODE)) {
            normalized = normalized.substring(COUNTRY_CODE.length());
        }
        requirePattern(normalized, PHONE_PATTERN, "Invalid Colombian phone number: " + value);
        return new PhoneNumber(normalized);
    }

    public boolean isMobile() {
        return value.startsWith("3");
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PhoneNumber that)) return false;
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
