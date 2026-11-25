package com.co.bank.credit.loan_origination_system.domain.creditproduct;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;

import java.util.Locale;

/**
 * Documentos que un producto puede exigir para radicar una solicitud.
 */
public enum RequirementType {

    IDENTITY_DOCUMENT("Documento de identidad"),
    EMPLOYMENT_CERTIFICATE("Certificado laboral"),
    PAY_STUBS("Desprendibles de pago"),
    BANK_STATEMENTS("Extractos bancarios"),
    VEHICLE_QUOTE("Cotización del vehículo");

    private final String description;

    RequirementType(String description) {
        this.description = description;
    }

    public static RequirementType of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("Requirement type cannot be null or blank");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid requirement type: " + value);
        }
    }

    public String getDescription() {
        return description;
    }
}
