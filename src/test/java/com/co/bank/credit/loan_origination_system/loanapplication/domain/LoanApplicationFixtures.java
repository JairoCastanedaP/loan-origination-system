package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProduct;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.InterestRate;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;
import com.co.bank.credit.loan_origination_system.creditproduct.domain.Term;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.shared.domain.model.Money;

import java.time.Instant;
import java.util.EnumSet;

/**
 * Datos de prueba compartidos: producto de libre inversión al 24% E.A.
 */
final class LoanApplicationFixtures {

    static final Instant NOW = Instant.parse("2026-11-25T15:00:00Z");
    static final Money STUDY_FEE = Money.of("80000");
    static final Money HIGH_INCOME = Money.of("5000000");
    static final Money LOW_INCOME = Money.of("2000000");
    static final CreditPolicy POLICY = CreditPolicy.standard();

    private LoanApplicationFixtures() {
    }

    static CreditProduct personalLoan() {
        return CreditProduct.create(CreditProductId.generate(), "Libre inversión", CreditProductType.PERSONAL_LOAN,
                InterestRate.ofAnnualPercentage("24"), Money.of("1000000"), Money.of("100000000"),
                Term.ofMonths(12), Term.ofMonths(60), STUDY_FEE,
                EnumSet.of(RequirementType.IDENTITY_DOCUMENT, RequirementType.EMPLOYMENT_CERTIFICATE));
    }

    static LoanApplication draft(String amount, int months) {
        return LoanApplication.create(LoanApplicationId.generate(), ApplicationNumber.of(2026, 1),
                CustomerId.generate(), personalLoan(), Money.of(amount), Term.ofMonths(months), NOW);
    }

    /**
     * Solicitud en estudio: requisitos cargados, radicada y con la tarifa pagada.
     */
    static LoanApplication underReview(String amount, int months) {
        LoanApplication application = draft(amount, months);
        application.attachRequirement(RequirementType.IDENTITY_DOCUMENT, "cc.pdf", NOW);
        application.attachRequirement(RequirementType.EMPLOYMENT_CERTIFICATE, "certificado.pdf", NOW);
        application.submit(NOW);
        application.registerStudyFeePayment(STUDY_FEE, "PAY-001", NOW);
        return application;
    }
}
