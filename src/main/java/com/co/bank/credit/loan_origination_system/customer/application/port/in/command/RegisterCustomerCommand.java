package com.co.bank.credit.loan_origination_system.customer.application.port.in.command;

import java.math.BigDecimal;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonBlank;
import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

public record RegisterCustomerCommand(
        String documentType,
        String documentNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        BigDecimal monthlyIncome) {

    public RegisterCustomerCommand {
        documentType = nonBlank(documentType, "documentType");
        documentNumber = nonBlank(documentNumber, "documentNumber");
        firstName = nonBlank(firstName, "firstName");
        lastName = nonBlank(lastName, "lastName");
        email = nonBlank(email, "email");
        phone = nonBlank(phone, "phone");
        nonNull(monthlyIncome, "monthlyIncome");
    }
}
