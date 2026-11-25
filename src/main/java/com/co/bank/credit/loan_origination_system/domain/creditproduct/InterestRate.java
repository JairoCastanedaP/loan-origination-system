package com.co.bank.credit.loan_origination_system.domain.creditproduct;

import com.co.bank.credit.loan_origination_system.domain.exceptions.ValueObjectException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requirePositive;

/**
 * Tasa de interés efectiva anual (E.A.), expresada en porcentaje: 24.5 significa 24,5% E.A.
 * La tasa mensual equivalente se obtiene con (1 + EA)^(1/12) - 1.
 */
public final class InterestRate {

    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal MAX_ANNUAL_PERCENTAGE = ONE_HUNDRED;
    private static final int RATE_SCALE = 10;

    private final BigDecimal annualPercentage;

    private InterestRate(BigDecimal annualPercentage) {
        this.annualPercentage = annualPercentage;
    }

    public static InterestRate ofAnnualPercentage(BigDecimal annualPercentage) {
        requirePositive(annualPercentage, "Annual interest rate");
        if (annualPercentage.compareTo(MAX_ANNUAL_PERCENTAGE) > 0) {
            throw new ValueObjectException("Annual interest rate cannot exceed " + MAX_ANNUAL_PERCENTAGE + "%");
        }
        return new InterestRate(annualPercentage.setScale(4, RoundingMode.HALF_EVEN));
    }

    public static InterestRate ofAnnualPercentage(String annualPercentage) {
        try {
            return ofAnnualPercentage(new BigDecimal(annualPercentage.trim()));
        } catch (NumberFormatException | NullPointerException e) {
            throw new ValueObjectException("Invalid annual interest rate: " + annualPercentage);
        }
    }

    public BigDecimal getAnnualPercentage() {
        return annualPercentage;
    }

    /**
     * Tasa efectiva anual como fracción (24.5% -> 0.245).
     */
    public BigDecimal annualFraction() {
        return annualPercentage.divide(ONE_HUNDRED, RATE_SCALE, RoundingMode.HALF_EVEN);
    }

    /**
     * Tasa mensual equivalente como fracción, con 16 dígitos significativos.
     * BigDecimal no tiene potencias fraccionarias, por eso la raíz doceava se calcula en double.
     * No se redondea a menos dígitos: con 10 decimales la cuota ya puede desviarse un centavo.
     */
    public BigDecimal monthlyFraction() {
        double monthly = Math.pow(1 + annualFraction().doubleValue(), 1.0 / 12) - 1;
        return new BigDecimal(monthly, MathContext.DECIMAL64);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof InterestRate that)) return false;
        return annualPercentage.equals(that.annualPercentage);
    }

    @Override
    public int hashCode() {
        return annualPercentage.hashCode();
    }

    @Override
    public String toString() {
        return annualPercentage.stripTrailingZeros().toPlainString() + "% E.A.";
    }
}
