package com.co.bank.credit.loan_origination_system.customer.application.mapper;

import com.co.bank.credit.loan_origination_system.customer.application.port.in.command.RegisterCustomerCommand;
import com.co.bank.credit.loan_origination_system.customer.domain.Customer;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Email;
import com.co.bank.credit.loan_origination_system.shared.domain.model.FirstName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.LastName;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import com.co.bank.credit.loan_origination_system.shared.domain.model.PhoneNumber;

public final class CustomerApplicationMapper {

    private CustomerApplicationMapper() {
    }

    public static DocumentNumber toDocument(String type, String number) {
        return DocumentNumber.of(type, number);
    }

    public static Customer toDomain(RegisterCustomerCommand command) {
        return Customer.create(
                CustomerId.generate(),
                toDocument(command.documentType(), command.documentNumber()),
                FirstName.of(command.firstName()),
                LastName.of(command.lastName()),
                Email.of(command.email()),
                PhoneNumber.of(command.phone()),
                Money.of(command.monthlyIncome()));
    }
}
