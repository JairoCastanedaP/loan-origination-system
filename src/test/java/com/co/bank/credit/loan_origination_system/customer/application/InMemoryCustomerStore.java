package com.co.bank.credit.loan_origination_system.customer.application;

import com.co.bank.credit.loan_origination_system.customer.application.port.in.model.CustomerView;
import com.co.bank.credit.loan_origination_system.customer.application.port.out.CustomerCommandPort;
import com.co.bank.credit.loan_origination_system.customer.application.port.out.CustomerQueryPort;
import com.co.bank.credit.loan_origination_system.customer.domain.Customer;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.shared.domain.model.DocumentNumber;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Doble de prueba de los puertos de salida de clientes.
 */
public class InMemoryCustomerStore implements CustomerCommandPort, CustomerQueryPort {

    private final Map<CustomerId, Customer> customers = new LinkedHashMap<>();

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public boolean existsByDocument(DocumentNumber document) {
        return customers.values().stream().anyMatch(c -> c.getDocument().equals(document));
    }

    @Override
    public void save(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public Optional<CustomerView> findViewById(CustomerId id) {
        return findById(id).map(CustomerView::from);
    }

    @Override
    public Optional<CustomerView> findViewByDocument(DocumentNumber document) {
        return customers.values().stream().filter(c -> c.getDocument().equals(document))
                .findFirst().map(CustomerView::from);
    }

    public int count() {
        return customers.size();
    }
}
