package com.co.bank.credit.loan_origination_system.creditproduct.application.service;

import com.co.bank.credit.loan_origination_system.creditproduct.application.mapper.CreditProductApplicationMapper;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.CreateCreditProductUseCase;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.command.CreateCreditProductCommand;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.out.CreditProductCommandPort;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;

import java.util.Objects;
import java.util.UUID;

public class CreateCreditProductService implements CreateCreditProductUseCase {

    private final CreditProductCommandPort creditProductCommandPort;

    public CreateCreditProductService(CreditProductCommandPort creditProductCommandPort) {
        this.creditProductCommandPort = Objects.requireNonNull(creditProductCommandPort,
                "creditProductCommandPort cannot be null");
    }

    @Override
    public UUID create(CreateCreditProductCommand command) {
        Objects.requireNonNull(command, "command cannot be null");
        CreditProduct product = CreditProductApplicationMapper.toDomain(command);
        creditProductCommandPort.save(product);
        return product.getId().getValue();
    }
}
