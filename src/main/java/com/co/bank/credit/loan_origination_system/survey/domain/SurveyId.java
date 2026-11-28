package com.co.bank.credit.loan_origination_system.survey.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;

import java.util.Objects;
import java.util.UUID;

public final class SurveyId {

    private static final UUID EMPTY = new UUID(0L, 0L);

    private final UUID value;

    private SurveyId(UUID value) {
        this.value = value;
    }

    public static SurveyId of(UUID value) {
        if (value == null) {
            throw new ValueObjectException("SurveyId cannot be null");
        }
        if (EMPTY.equals(value)) {
            throw new ValueObjectException("SurveyId cannot be an empty UUID");
        }
        return new SurveyId(value);
    }

    public static SurveyId of(String value) {
        if (value == null || value.isBlank()) {
            throw new ValueObjectException("SurveyId cannot be null or blank");
        }
        try {
            return of(UUID.fromString(value.trim()));
        } catch (IllegalArgumentException e) {
            throw new ValueObjectException("Invalid SurveyId: " + value);
        }
    }

    public static SurveyId generate() {
        return new SurveyId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SurveyId that)) return false;
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
