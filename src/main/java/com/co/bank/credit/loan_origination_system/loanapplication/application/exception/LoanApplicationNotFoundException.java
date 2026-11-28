package com.co.bank.credit.loan_origination_system.loanapplication.application.exception;

import com.co.bank.credit.loan_origination_system.shared.application.exception.ResourceNotFoundException;

public class LoanApplicationNotFoundException extends ResourceNotFoundException {

    public LoanApplicationNotFoundException(Object id) {
        super("Loan application", id);
    }
}
