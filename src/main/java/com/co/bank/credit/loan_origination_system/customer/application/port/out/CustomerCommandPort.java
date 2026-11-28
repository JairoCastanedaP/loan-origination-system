package com.co.bank.credit.loan_origination_system.customer.application.port.out;

import com.co.bank.credit.loan_origination_system.customer.domain.Customer;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;

import java.util.Optional;

/**
 * Escritura del agregado Customer: cargarlo para modificarlo y guardarlo.
 */
public interface CustomerCommandPort {

    Optional<Customer> findById(CustomerId id);

    boolean existsByDocument(DocumentNumber document);

    void save(Customer customer);
}
