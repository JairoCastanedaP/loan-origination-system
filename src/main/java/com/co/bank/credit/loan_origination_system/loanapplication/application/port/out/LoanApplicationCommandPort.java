package com.co.bank.credit.loan_origination_system.loanapplication.application.port.out;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.CreditProductId;
import com.co.bank.credit.loan_origination_system.customer.domain.CustomerId;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplication;
import com.co.bank.credit.loan_origination_system.loanapplication.domain.LoanApplicationId;

import java.util.Optional;

public interface LoanApplicationCommandPort {

    Optional<LoanApplication> findById(LoanApplicationId id);

    /**
     * RN-12: indica si el cliente ya tiene una solicitud no terminal del mismo producto.
     */
    boolean existsOpenApplication(CustomerId customerId, CreditProductId productId);

    void save(LoanApplication application);
}
