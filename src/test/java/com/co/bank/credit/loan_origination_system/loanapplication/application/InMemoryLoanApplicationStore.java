package com.co.bank.credit.loan_origination_system.loanapplication.application;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.out.ApplicationNumberGeneratorPort;
import com.co.bank.credit.loan_origination_system.loanapplication.application.port.out.LoanApplicationCommandPort;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.ApplicationNumber;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplication;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Doble de prueba del repositorio de solicitudes y del generador de números por año.
 */
public class InMemoryLoanApplicationStore implements LoanApplicationCommandPort, ApplicationNumberGeneratorPort {

    private final Map<LoanApplicationId, LoanApplication> applications = new LinkedHashMap<>();
    private final Map<Integer, Integer> sequences = new HashMap<>();
    private int saves;

    @Override
    public Optional<LoanApplication> findById(LoanApplicationId id) {
        return Optional.ofNullable(applications.get(id));
    }

    @Override
    public boolean existsOpenApplication(CustomerId customerId, CreditProductId productId) {
        return applications.values().stream().anyMatch(a -> a.getCustomerId().equals(customerId)
                && a.getProductId().equals(productId)
                && !a.getStatus().isTerminal());
    }

    @Override
    public void save(LoanApplication application) {
        applications.put(application.getId(), application);
        saves++;
    }

    @Override
    public ApplicationNumber next(int year) {
        return ApplicationNumber.of(year, sequences.merge(year, 1, Integer::sum));
    }

    public int lastSequence(int year) {
        return sequences.getOrDefault(year, 0);
    }

    public int saves() {
        return saves;
    }
}
