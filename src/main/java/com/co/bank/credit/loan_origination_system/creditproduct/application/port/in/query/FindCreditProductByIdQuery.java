package com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.query;

import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

public record FindCreditProductByIdQuery(UUID creditProductId) {

    public FindCreditProductByIdQuery {
        nonNull(creditProductId, "creditProductId");
    }
}
