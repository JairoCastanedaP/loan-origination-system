package com.co.bank.credit.loan_origination_system.creditproduct.application.mapper;

import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.command.CreateCreditProductCommand;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.InterestRate;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public final class CreditProductApplicationMapper {

    private CreditProductApplicationMapper() {
    }

    public static CreditProduct toDomain(CreateCreditProductCommand command) {
        return CreditProduct.create(
                CreditProductId.generate(),
                command.name(),
                CreditProductType.of(command.type()),
                InterestRate.ofAnnualPercentage(command.annualInterestRate()),
                Money.of(command.minAmount()),
                Money.of(command.maxAmount()),
                Term.ofMonths(command.minTermMonths()),
                Term.ofMonths(command.maxTermMonths()),
                Money.of(command.studyFee()),
                toRequirements(command.requiredRequirements()));
    }

    private static Set<RequirementType> toRequirements(Set<String> values) {
        return values.stream().map(RequirementType::of)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(RequirementType.class)));
    }
}
