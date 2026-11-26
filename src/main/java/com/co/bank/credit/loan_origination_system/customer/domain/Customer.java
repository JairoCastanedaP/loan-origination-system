package com.co.bank.credit.loan_origination_system.customer.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.shared.domain.kernel.AggregateRoot;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Email;
import com.co.bank.credit.loan_origination_system.shared.domain.model.FirstName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.LastName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import com.co.bank.credit.loan_origination_system.shared.domain.model.PhoneNumber;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

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
