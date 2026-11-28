package com.co.bank.credit.loan_origination_system.customer.application.port.in.query;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonBlank;

public record FindCustomerByDocumentQuery(String documentType, String documentNumber) {

    public FindCustomerByDocumentQuery {
        documentType = nonBlank(documentType, "documentType");
        documentNumber = nonBlank(documentNumber, "documentNumber");
    }
}
