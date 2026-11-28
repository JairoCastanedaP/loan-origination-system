package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.model;

import java.util.UUID;

/**
 * Identificador técnico y número legible de la solicitud recién creada (RF-05).
 */
public record LoanApplicationReference(UUID id, String applicationNumber) {
}
