package com.co.bank.credit.loan_origination_system.customer.application.port.in;

import com.co.bank.credit.loan_origination_system.customer.application.port.in.model.CustomerView;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.query.FindCustomerByDocumentQuery;

/**
 * RF-02: consultar un cliente por documento.
 */
@FunctionalInterface
public interface FindCustomerByDocumentUseCase {

    CustomerView findByDocument(FindCustomerByDocumentQuery query);
}
