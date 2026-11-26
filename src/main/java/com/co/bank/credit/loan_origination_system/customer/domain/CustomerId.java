package com.co.bank.credit.loan_origination_system.customer.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador de un cliente.
 */
public final class CustomerId {

    private static final UUID EMPTY = new UUID(0L, 0L);

    private final UUID value;

    private CustomerId(UUID value) {
        this.value = value;
    }

    public static CustomerId of(UUID value) {
        if (value == null) {
            throw new ValueObjectException("CustomerId cannot be null");
        }
        if (EMPTY.equals(value)) {
            throw new ValueObjectException("CustomerId cannot be an empty UUID");
        }
        return new CustomerId(value);
    }

    public static CustomerId of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("CustomerId cannot be null or blank");
        }
        try {
            return of(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid CustomerId: " + value);
        }
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CustomerId that)) return false;
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
