package com.co.bank.credit.loan_origination_system.loanapplication.application.port.in.command;

import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonBlank;
import static com.co.bank.credit.loan_origination_system.shared.application.validation.Require.nonNull;

/**
 * @param documentReference referencia al documento cargado (nombre o ruta del archivo almacenado)
 */
public record AttachRequirementCommand(UUID loanApplicationId, String requirementType, String documentReference) {

    public AttachRequirementCommand {
        nonNull(loanApplicationId, "loanApplicationId");
        requirementType = nonBlank(requirementType, "requirementType");
        documentReference = nonBlank(documentReference, "documentReference");
    }
}
