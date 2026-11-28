package com.co.bank.credit.loan_origination_system.creditproduct.application.port.out;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;

import java.util.Optional;

public interface CreditProductCommandPort {

    Optional<CreditProduct> findById(CreditProductId id);

    void save(CreditProduct product);
}
