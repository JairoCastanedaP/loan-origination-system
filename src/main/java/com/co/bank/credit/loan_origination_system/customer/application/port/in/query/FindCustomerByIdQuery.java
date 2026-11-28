package com.co.bank.credit.loan_origination_system.customer.application.port.in.query;

import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

public record FindCustomerByIdQuery(UUID customerId) {

    public FindCustomerByIdQuery {
        nonNull(customerId, "customerId");
    }
}
