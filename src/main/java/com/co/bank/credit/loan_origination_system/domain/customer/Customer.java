package com.co.bank.credit.loan_origination_system.domain.customer;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;
import com.co.bank.credit.loan_origination_system.domain.generic.AggregateRoot;
import com.co.bank.credit.loan_origination_system.domain.shared.DocumentNumber;
import com.co.bank.credit.loan_origination_system.domain.shared.Email;
import com.co.bank.credit.loan_origination_system.domain.shared.FirstName;
import com.co.bank.credit.loan_origination_system.domain.shared.LastName;
import com.co.bank.credit.loan_origination_system.domain.shared.Money;
import com.co.bank.credit.loan_origination_system.domain.shared.PhoneNumber;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Cliente que solicita créditos. Su ingreso mensual se usa para calcular la capacidad de pago.
 */
public final class Customer extends AggregateRoot<CustomerId> {

    private final DocumentNumber document;
    private final FirstName firstName;
    private final LastName lastName;
    private Email email;
    private PhoneNumber phone;
    private Money monthlyIncome;

    private Customer(CustomerId id, DocumentNumber document, FirstName firstName, LastName lastName,
                     Email email, PhoneNumber phone, Money monthlyIncome) {
        super(id);
        this.document = requireNonNull(document, "Document cannot be null");
        this.firstName = requireNonNull(firstName, "First name cannot be null");
        this.lastName = requireNonNull(lastName, "Last name cannot be null");
        this.email = requireNonNull(email, "Email cannot be null");
        this.phone = requireNonNull(phone, "Phone cannot be null");
        this.monthlyIncome = requirePositiveIncome(monthlyIncome);
    }

    public static Customer create(CustomerId id, DocumentNumber document, FirstName firstName, LastName lastName,
                                  Email email, PhoneNumber phone, Money monthlyIncome) {
        return new Customer(id, document, firstName, lastName, email, phone, monthlyIncome);
    }

    public static Customer reconstitute(CustomerId id, DocumentNumber document, FirstName firstName,
                                        LastName lastName, Email email, PhoneNumber phone, Money monthlyIncome) {
        return new Customer(id, document, firstName, lastName, email, phone, monthlyIncome);
    }

    public void updateContact(Email newEmail, PhoneNumber newPhone) {
        this.email = requireNonNull(newEmail, "Email cannot be null");
        this.phone = requireNonNull(newPhone, "Phone cannot be null");
    }

    public void updateMonthlyIncome(Money newIncome) {
        this.monthlyIncome = requirePositiveIncome(newIncome);
    }

    public String fullName() {
        return firstName.getValue() + " " + lastName.getValue();
    }

    private static Money requirePositiveIncome(Money income) {
        requireNonNull(income, "Monthly income cannot be null");
        if (income.isZero()) {
            throw new ValueObjectException("Monthly income must be greater than zero");
        }
        return income;
    }

    public DocumentNumber getDocument() { return document; }
    public FirstName getFirstName() { return firstName; }
    public LastName getLastName() { return lastName; }
    public Email getEmail() { return email; }
    public PhoneNumber getPhone() { return phone; }
    public Money getMonthlyIncome() { return monthlyIncome; }
}
