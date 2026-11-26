package com.co.bank.credit.loan_origination_system.creditproduct.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador de un producto de crédito.
 */
public final class CreditProductId {

    private static final UUID EMPTY = new UUID(0L, 0L);

    private final UUID value;

    private CreditProductId(UUID value) {
        this.value = value;
    }

    public static CreditProductId of(UUID value) {
        if (value == null) {
            throw new ValueObjectException("CreditProductId cannot be null");
        }
        if (EMPTY.equals(value)) {
            throw new ValueObjectException("CreditProductId cannot be an empty UUID");
        }
        return new CreditProductId(value);
    }

    public static CreditProductId of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("CreditProductId cannot be null or blank");
        }
        try {
            return of(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid CreditProductId: " + value);
        }
    }

    public static CreditProductId generate() {
        return new CreditProductId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CreditProductId that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
