package com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.command;

import java.math.BigDecimal;
import java.util.Set;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonBlank;
import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

public record CreateCreditProductCommand(
        String name,
        String type,
        BigDecimal annualInterestRate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Integer minTermMonths,
        Integer maxTermMonths,
        BigDecimal studyFee,
        Set<String> requiredRequirements) {

    public CreateCreditProductCommand {
        name = nonBlank(name, "name");
        type = nonBlank(type, "type");
        nonNull(annualInterestRate, "annualInterestRate");
        nonNull(minAmount, "minAmount");
        nonNull(maxAmount, "maxAmount");
        nonNull(minTermMonths, "minTermMonths");
        nonNull(maxTermMonths, "maxTermMonths");
        nonNull(studyFee, "studyFee");
        requiredRequirements = Set.copyOf(nonNull(requiredRequirements, "requiredRequirements"));
    }
}
