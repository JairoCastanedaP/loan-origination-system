package com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.model;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreditProductView(
        UUID id,
        String name,
        String type,
        BigDecimal annualInterestRate,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        int minTermMonths,
        int maxTermMonths,
        BigDecimal studyFee,
        Set<String> requiredRequirements,
        boolean active) {

    public static CreditProductView from(CreditProduct product) {
        return new CreditProductView(
                product.getId().getValue(),
                product.getName(),
                product.getType().name(),
                product.getInterestRate().getAnnualPercentage(),
                product.getMinAmount().getAmount(),
                product.getMaxAmount().getAmount(),
                product.getMinTerm().getMonths(),
                product.getMaxTerm().getMonths(),
                product.getStudyFee().getAmount(),
                product.getRequiredRequirements().stream().map(RequirementType::name)
                        .collect(Collectors.toUnmodifiableSet()),
                product.isActive());
    }
}
