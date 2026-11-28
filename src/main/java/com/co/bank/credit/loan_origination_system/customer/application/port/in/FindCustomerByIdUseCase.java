package com.co.bank.credit.loan_origination_system.customer.application.port.in;

import com.co.bank.credit.loan_origination_system.customer.application.port.in.model.CustomerView;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.query.FindCustomerByIdQuery;

@FunctionalInterface
public interface FindCustomerByIdUseCase {

    CustomerView findById(FindCustomerByIdQuery query);
}
