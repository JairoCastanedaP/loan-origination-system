package com.co.bank.credit.loan_origination_system.creditproduct.application;

import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.model.CreditProductView;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.out.CreditProductCommandPort;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.out.CreditProductQueryPort;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Doble de prueba de los puertos de salida de productos.
 */
public class InMemoryCreditProductStore implements CreditProductCommandPort, CreditProductQueryPort {

    private final Map<CreditProductId, CreditProduct> products = new LinkedHashMap<>();

    @Override
    public Optional<CreditProduct> findById(CreditProductId id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public void save(CreditProduct product) {
        products.put(product.getId(), product);
    }

    @Override
    public Optional<CreditProductView> findViewById(CreditProductId id) {
        return findById(id).map(CreditProductView::from);
    }
}
