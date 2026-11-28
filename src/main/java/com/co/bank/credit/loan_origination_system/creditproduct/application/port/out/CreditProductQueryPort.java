package com.co.bank.credit.loan_origination_system.creditproduct.application.port.out;

import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.model.CreditProductView;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;

import java.util.Optional;

public interface CreditProductQueryPort {

    Optional<CreditProductView> findViewById(CreditProductId id);
}
