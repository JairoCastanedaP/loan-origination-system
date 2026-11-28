package com.co.bank.credit.loan_origination_system.customer.application.port.out;

import com.co.bank.credit.loan_origination_system.customer.application.port.in.model.CustomerView;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;

import java.util.Optional;

/**
 * Lecturas de clientes como vistas, sin reconstruir el agregado.
 */
public interface CustomerQueryPort {

    Optional<CustomerView> findViewById(CustomerId id);

    Optional<CustomerView> findViewByDocument(DocumentNumber document);
}
