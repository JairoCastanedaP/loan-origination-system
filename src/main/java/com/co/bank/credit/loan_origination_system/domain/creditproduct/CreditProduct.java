package com.co.bank.credit.loan_origination_system.domain.creditproduct;

import com.co.bank.credit.loan_origination_system.domain.exceptions.BusinessRuleException;
import com.co.bank.credit.loan_origination_system.domain.exceptions.EntityException;
import com.co.bank.credit.loan_origination_system.domain.generic.AggregateRoot;
import com.co.bank.credit.loan_origination_system.domain.shared.Money;

import java.util.EnumSet;
import java.util.Set;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Producto de crédito: define la tasa, los montos y plazos permitidos,
 * la tarifa de estudio y los requisitos documentales.
 */
public final class CreditProduct extends AggregateRoot<CreditProductId> {

    private final String name;
    private final CreditProductType type;
    private InterestRate interestRate;
    private final Money minAmount;
    private final Money maxAmount;
    private final Term minTerm;
    private final Term maxTerm;
    private Money studyFee;
    private final Set<RequirementType> requiredRequirements;
    private boolean active;

    private CreditProduct(CreditProductId id, String name, CreditProductType type, InterestRate interestRate,
                          Money minAmount, Money maxAmount, Term minTerm, Term maxTerm, Money studyFee,
                          Set<RequirementType> requiredRequirements, boolean active) {
        super(id);
        this.name = requireLength(requireNonBlank(name, "Product name cannot be null or blank"), 3, 80, "Product name");
        this.type = requireNonNull(type, "Product type cannot be null");
        this.interestRate = requireNonNull(interestRate, "Interest rate cannot be null");
        this.minAmount = requireNonNull(minAmount, "Minimum amount cannot be null");
        this.maxAmount = requireNonNull(maxAmount, "Maximum amount cannot be null");
        this.minTerm = requireNonNull(minTerm, "Minimum term cannot be null");
        this.maxTerm = requireNonNull(maxTerm, "Maximum term cannot be null");
        this.studyFee = requireNonNull(studyFee, "Study fee cannot be null");
        requireNonNull(requiredRequirements, "Required requirements cannot be null");

        if (minAmount.isZero() || minAmount.isGreaterThan(maxAmount)) {
            throw new EntityException("Minimum amount must be positive and not greater than maximum amount");
        }
        if (minTerm.compareTo(maxTerm) > 0) {
            throw new EntityException("Minimum term cannot be greater than maximum term");
        }
        if (requiredRequirements.isEmpty()) {
            throw new EntityException("A credit product must require at least one document");
        }
        this.requiredRequirements = EnumSet.copyOf(requiredRequirements);
        this.active = active;
    }

    public static CreditProduct create(CreditProductId id, String name, CreditProductType type,
                                       InterestRate interestRate, Money minAmount, Money maxAmount,
                                       Term minTerm, Term maxTerm, Money studyFee,
                                       Set<RequirementType> requiredRequirements) {
        return new CreditProduct(id, name, type, interestRate, minAmount, maxAmount, minTerm, maxTerm,
                studyFee, requiredRequirements, true);
    }

    public static CreditProduct reconstitute(CreditProductId id, String name, CreditProductType type,
                                             InterestRate interestRate, Money minAmount, Money maxAmount,
                                             Term minTerm, Term maxTerm, Money studyFee,
                                             Set<RequirementType> requiredRequirements, boolean active) {
        return new CreditProduct(id, name, type, interestRate, minAmount, maxAmount, minTerm, maxTerm,
                studyFee, requiredRequirements, active);
    }

    /**
     * RN-01: el monto y el plazo deben estar dentro de los límites del producto.
     */
    public void validateConditions(Money amount, Term term) {
        requireNonNull(amount, "Requested amount cannot be null");
        requireNonNull(term, "Requested term cannot be null");
        if (!amount.isBetween(minAmount, maxAmount)) {
            throw new BusinessRuleException("RN-01",
                    "Amount " + amount + " is outside the product range " + minAmount + " - " + maxAmount);
        }
        if (!term.isBetween(minTerm, maxTerm)) {
            throw new BusinessRuleException("RN-01",
                    "Term " + term + " is outside the product range " + minTerm + " - " + maxTerm);
        }
    }

    public void ensureActive() {
        if (!active) {
            throw new BusinessRuleException("PRODUCT_INACTIVE", "Credit product " + name + " is not active");
        }
    }

    public void changeInterestRate(InterestRate newRate) {
        this.interestRate = requireNonNull(newRate, "Interest rate cannot be null");
    }

    public void changeStudyFee(Money newFee) {
        this.studyFee = requireNonNull(newFee, "Study fee cannot be null");
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public String getName() { return name; }
    public CreditProductType getType() { return type; }
    public InterestRate getInterestRate() { return interestRate; }
    public Money getMinAmount() { return minAmount; }
    public Money getMaxAmount() { return maxAmount; }
    public Term getMinTerm() { return minTerm; }
    public Term getMaxTerm() { return maxTerm; }
    public Money getStudyFee() { return studyFee; }
    public Set<RequirementType> getRequiredRequirements() { return Set.copyOf(requiredRequirements); }
    public boolean isActive() { return active; }
}
