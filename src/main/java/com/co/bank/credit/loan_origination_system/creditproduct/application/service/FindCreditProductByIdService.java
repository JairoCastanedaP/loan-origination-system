package com.co.bank.credit.loan_origination_system.creditproduct.application.service;

import com.co.bank.credit.loan_origination_system.creditproduct.application.exception.CreditProductNotFoundException;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.FindCreditProductByIdUseCase;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.model.CreditProductView;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.query.FindCreditProductByIdQuery;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.out.CreditProductQueryPort;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;

import java.util.Objects;

public class FindCreditProductByIdService implements FindCreditProductByIdUseCase {

    private final CreditProductQueryPort creditProductQueryPort;

    public FindCreditProductByIdService(CreditProductQueryPort creditProductQueryPort) {
        this.creditProductQueryPort = Objects.requireNonNull(creditProductQueryPort,
                "creditProductQueryPort cannot be null");
    }

    @Override
    public CreditProductView findById(FindCreditProductByIdQuery query) {
        Objects.requireNonNull(query, "query cannot be null");
        return creditProductQueryPort.findViewById(CreditProductId.of(query.creditProductId()))
                .orElseThrow(() -> new CreditProductNotFoundException(query.creditProductId()));
    }
}
