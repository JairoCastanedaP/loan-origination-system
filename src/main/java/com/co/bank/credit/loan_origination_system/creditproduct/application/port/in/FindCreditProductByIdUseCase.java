package com.co.bank.credit.loan_origination_system.creditproduct.application.port.in;

import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.model.CreditProductView;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.query.FindCreditProductByIdQuery;

@FunctionalInterface
public interface FindCreditProductByIdUseCase {

    CreditProductView findById(FindCreditProductByIdQuery query);
}
