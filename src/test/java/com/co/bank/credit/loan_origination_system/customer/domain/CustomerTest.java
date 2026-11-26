package com.co.bank.credit.loan_origination_system.customer.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentType;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Email;
import com.co.bank.credit.loan_origination_system.shared.domain.model.FirstName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.LastName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import com.co.bank.credit.loan_origination_system.shared.domain.model.PhoneNumber;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    private static Customer customer(CustomerId id, String income) {
        return Customer.create(id, DocumentNumber.of(DocumentType.CC, "1020304050"), FirstName.of("María José"),
                LastName.of("Rodríguez"), Email.of("maria@example.com"), PhoneNumber.of("3001234567"),
                Money.of(income));
    }

    @Test
    void createsCustomerWithFullName() {
        Customer customer = customer(CustomerId.generate(), "4500000");

        assertThat(customer.fullName()).isEqualTo("María José Rodríguez");
        assertThat(customer.getMonthlyIncome()).isEqualTo(Money.of("4500000"));
    }

    @Test
    void monthlyIncomeMustBePositive() {
        assertThatThrownBy(() -> customer(CustomerId.generate(), "0")).isInstanceOf(ValueObjectException.class);

        Customer customer = customer(CustomerId.generate(), "4500000");
        assertThatThrownBy(() -> customer.updateMonthlyIncome(Money.zero())).isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> customer.updateMonthlyIncome(null)).isInstanceOf(ValueObjectException.class);

        customer.updateMonthlyIncome(Money.of("5200000"));
        assertThat(customer.getMonthlyIncome()).isEqualTo(Money.of("5200000"));
    }

    @Test
    void updatesContactData() {
        Customer customer = customer(CustomerId.generate(), "4500000");

        customer.updateContact(Email.of("mj.rodriguez@example.com"), PhoneNumber.of("6017654321"));

        assertThat(customer.getEmail()).isEqualTo(Email.of("mj.rodriguez@example.com"));
        assertThat(customer.getPhone().isMobile()).isFalse();
        assertThatThrownBy(() -> customer.updateContact(null, PhoneNumber.of("3001234567")))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void customersWithTheSameIdAreTheSameCustomer() {
        CustomerId id = CustomerId.generate();

        assertThat(customer(id, "4500000")).isEqualTo(customer(id, "9000000"));
        assertThat(customer(id, "4500000")).isNotEqualTo(customer(CustomerId.generate(), "4500000"));
    }

    @Test
    void requiresAllData() {
        assertThatThrownBy(() -> Customer.create(CustomerId.generate(), null, FirstName.of("Ana"),
                LastName.of("Pérez"), Email.of("ana@example.com"), PhoneNumber.of("3001234567"), Money.of("1")))
                .isInstanceOf(ValueObjectException.class);
    }
}
