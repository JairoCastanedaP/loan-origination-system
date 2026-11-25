package com.co.bank.credit.loan_origination_system.domain.shared;

/**
 * Apellido(s) de una persona. Ver {@link PersonNameRules}.
 */
public final class LastName {

    private final String value;

    private LastName(String value) {
        this.value = value;
    }

    public static LastName of(String value) {
        return new LastName(PersonNameRules.normalize(value, "LastName"));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LastName that)) return false;
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
