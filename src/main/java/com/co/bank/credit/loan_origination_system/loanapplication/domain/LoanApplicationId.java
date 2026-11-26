package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;

import java.util.Objects;
import java.util.UUID;

/**
 * Identificador de una solicitud de crédito.
 */
public final class LoanApplicationId {

    private static final UUID EMPTY = new UUID(0L, 0L);

    private final UUID value;

    private LoanApplicationId(UUID value) {
        this.value = value;
    }

    public static LoanApplicationId of(UUID value) {
        if (value == null) {
            throw new ValueObjectException("LoanApplicationId cannot be null");
        }
        if (EMPTY.equals(value)) {
            throw new ValueObjectException("LoanApplicationId cannot be an empty UUID");
        }
        return new LoanApplicationId(value);
    }

    public static LoanApplicationId of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("LoanApplicationId cannot be null or blank");
        }
        try {
            return of(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid LoanApplicationId: " + value);
        }
    }

    public static LoanApplicationId generate() {
        return new LoanApplicationId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LoanApplicationId that)) return false;
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
