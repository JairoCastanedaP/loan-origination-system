package com.co.bank.credit.loan_origination_system.loanapplication.application.port.out;

import com.co.bank.credit.loan_origination_system.loanapplication.domain.ApplicationNumber;

/**
 * RF-05: entrega el siguiente número consecutivo del año. En la fase 06 lo respalda una secuencia de la base de datos.
 */
@FunctionalInterface
public interface ApplicationNumberGeneratorPort {

    ApplicationNumber next(int year);
}
