package com.co.bank.credit.loan_origination_system.domain.shared;

/**
 * Nombre(s) de una persona. Ver {@link PersonNameRules}.
 */
public final class FirstName {

    private final String value;

    private FirstName(String value) {
        this.value = value;
    }

    public static FirstName of(String value) {
        return new FirstName(PersonNameRules.normalize(value, "FirstName"));
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FirstName that)) return false;
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
