package com.co.bank.credit.loan_origination_system.domain.shared;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Documento de identidad: tipo + número. El número se normaliza en mayúsculas
 * y sin separadores (puntos, espacios, guiones), por ejemplo "1.020.304" -> "1020304".
 */
public final class DocumentNumber {

    private static final Pattern SEPARATORS = Pattern.compile("[\\s.-]");

    private final DocumentType type;
    private final String number;

    private DocumentNumber(DocumentType type, String number) {
        this.type = type;
        this.number = number;
    }

    public static DocumentNumber of(DocumentType type, String number) {
        requireNonNull(type, "Document type cannot be null");
        String normalized = SEPARATORS.matcher(requireNonBlank(number, "Document number cannot be null or blank"))
                .replaceAll("")
                .toUpperCase(Locale.ROOT);
        if (!type.isValidNumber(normalized)) {
            throw new ValueObjectException("Invalid " + type.name() + " number: " + number);
        }
        return new DocumentNumber(type, normalized);
    }

    public static DocumentNumber of(String type, String number) {
        return of(DocumentType.of(type), number);
    }

    public DocumentType getType() {
        return type;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DocumentNumber that)) return false;
        return type == that.type && number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, number);
    }

    @Override
    public String toString() {
        return type.name() + "-" + number;
    }
}
