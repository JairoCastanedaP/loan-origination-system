package com.co.bank.credit.loan_origination_system.customer.application.port.in.model;

import com.co.bank.credit.loan_origination_system.customer.domain.Customer;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Vista de solo lectura de un cliente. No expone el agregado fuera de la aplicación.
 */
public record CustomerView(
        UUID id,
        String documentType,
        String documentNumber,
        String firstName,
        String lastName,
        String fullName,
        String email,
        String phone,
        BigDecimal monthlyIncome,
        String currency) {

    public static CustomerView from(Customer customer) {
        return new CustomerView(
                customer.getId().getValue(),
                customer.getDocument().getType().name(),
                customer.getDocument().getNumber(),
                customer.getFirstName().getValue(),
                customer.getLastName().getValue(),
                customer.fullName(),
                customer.getEmail().getValue(),
                customer.getPhone().getValue(),
                customer.getMonthlyIncome().getAmount(),
                customer.getMonthlyIncome().getCurrency().getCurrencyCode());
    }
}
