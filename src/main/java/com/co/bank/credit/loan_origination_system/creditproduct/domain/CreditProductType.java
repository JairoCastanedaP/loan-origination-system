package com.co.bank.credit.loan_origination_system.creditproduct.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;

import java.util.Locale;

/**
 * Tipos de crédito que ofrece la entidad.
 */
public enum CreditProductType {

    PERSONAL_LOAN("Libre inversión"),
    VEHICLE_LOAN("Vehículo");

    private final String description;

    CreditProductType(String description) {
        this.description = description;
    }

    public static CreditProductType of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("Credit product type cannot be null or blank");
        }
        try {
            return valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid credit product type: " + value);
        }
    }

    public String getDescription() {
        return description;
    }
}
