package com.co.bank.credit.loan_origination_system.creditproduct.domain;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.EntityException;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.EnumSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditProductTest {

    private static final Set<RequirementType> REQUIREMENTS =
            EnumSet.of(RequirementType.IDENTITY_DOCUMENT, RequirementType.VEHICLE_QUOTE);

    /** Producto de vehículo: 5M a 150M, 12 a 72 meses. */
    private static CreditProduct vehicleLoan() {
        return product("5000000", "150000000", 12, 72, REQUIREMENTS);
    }

    private static CreditProduct product(String min, String max, int minTerm, int maxTerm,
                                         Set<RequirementType> requirements) {
        return CreditProduct.create(CreditProductId.generate(), "Crédito de vehículo", CreditProductType.VEHICLE_LOAN,
                InterestRate.ofAnnualPercentage("19.5"), Money.of(min), Money.of(max),
                Term.ofMonths(minTerm), Term.ofMonths(maxTerm), Money.of("120000"), requirements);
    }

    @Test
    void isCreatedActive() {
        CreditProduct product = vehicleLoan();

        assertThat(product.isActive()).isTrue();
        assertThat(product.getType()).isEqualTo(CreditProductType.VEHICLE_LOAN);
        assertThat(product.getRequiredRequirements()).containsExactlyInAnyOrderElementsOf(REQUIREMENTS);
    }

    @ParameterizedTest(name = "{0} a {1} meses")
    @CsvSource({"5000000,12", "150000000,72", "5000000,72", "80000000,36"})
    void rn01AcceptsConditionsOnTheLimits(String amount, int months) {
        assertThatCode(() -> vehicleLoan().validateConditions(Money.of(amount), Term.ofMonths(months)))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "{0} a {1} meses")
    @CsvSource({"4999999.99,12", "150000000.01,72", "80000000,11", "80000000,73"})
    void rn01RejectsConditionsJustOutsideTheLimits(String amount, int months) {
        assertThatThrownBy(() -> vehicleLoan().validateConditions(Money.of(amount), Term.ofMonths(months)))
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("RN-01");
    }

    @Test
    void inactiveProductCannotTakeNewApplications() {
        CreditProduct product = vehicleLoan();
        product.deactivate();

        assertThatThrownBy(product::ensureActive)
                .isInstanceOf(BusinessRuleException.class).extracting("ruleCode").isEqualTo("PRODUCT_INACTIVE");

        product.activate();
        assertThatCode(product::ensureActive).doesNotThrowAnyException();
    }

    @Test
    void reconstituteKeepsTheStoredState() {
        CreditProduct product = CreditProduct.reconstitute(CreditProductId.generate(), "Libre inversión",
                CreditProductType.PERSONAL_LOAN, InterestRate.ofAnnualPercentage("24"), Money.of("1000000"),
                Money.of("100000000"), Term.ofMonths(12), Term.ofMonths(60), Money.of("80000"),
                EnumSet.of(RequirementType.IDENTITY_DOCUMENT), false);

        assertThat(product.isActive()).isFalse();
    }

    @Test
    void changesRateAndFee() {
        CreditProduct product = vehicleLoan();

        product.changeInterestRate(InterestRate.ofAnnualPercentage("21"));
        product.changeStudyFee(Money.of("150000"));

        assertThat(product.getInterestRate()).isEqualTo(InterestRate.ofAnnualPercentage("21"));
        assertThat(product.getStudyFee()).isEqualTo(Money.of("150000"));
        assertThatThrownBy(() -> product.changeInterestRate(null)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void rejectsInconsistentRanges() {
        assertThatThrownBy(() -> product("100", "50", 12, 72, REQUIREMENTS)).isInstanceOf(EntityException.class);
        assertThatThrownBy(() -> product("0", "50", 12, 72, REQUIREMENTS)).isInstanceOf(EntityException.class);
        assertThatThrownBy(() -> product("5000000", "150000000", 72, 12, REQUIREMENTS)).isInstanceOf(EntityException.class);
        assertThatThrownBy(() -> product("5000000", "150000000", 12, 72, EnumSet.noneOf(RequirementType.class)))
                .isInstanceOf(EntityException.class);
    }

    @Test
    void rejectsInvalidNames() {
        assertThatThrownBy(() -> CreditProduct.create(CreditProductId.generate(), "AB", CreditProductType.PERSONAL_LOAN,
                InterestRate.ofAnnualPercentage("24"), Money.of("1"), Money.of("2"), Term.ofMonths(1), Term.ofMonths(2),
                Money.of("0"), REQUIREMENTS)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void requirementsCannotBeChangedFromOutside() {
        Set<RequirementType> original = EnumSet.of(RequirementType.IDENTITY_DOCUMENT);
        CreditProduct product = product("5000000", "150000000", 12, 72, original);

        original.add(RequirementType.PAY_STUBS);

        assertThat(product.getRequiredRequirements()).containsExactly(RequirementType.IDENTITY_DOCUMENT);
        assertThatThrownBy(() -> product.getRequiredRequirements().add(RequirementType.PAY_STUBS))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
