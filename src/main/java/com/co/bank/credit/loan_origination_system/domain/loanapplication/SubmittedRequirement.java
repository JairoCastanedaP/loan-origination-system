package com.co.bank.credit.loan_origination_system.domain.loanapplication;

import com.co.bank.credit.loan_origination_system.domain.creditproduct.RequirementType;

import java.time.Instant;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Documento cargado por el cliente para cumplir un requisito.
 * La referencia identifica el archivo (nombre, ruta o URL), no su contenido.
 */
public record SubmittedRequirement(RequirementType type, String documentReference, Instant submittedAt) {

    public SubmittedRequirement {
        requireNonNull(type, "Requirement type cannot be null");
        documentReference = requireLength(
                requireNonBlank(documentReference, "Document reference cannot be null or blank"),
                1, 255, "Document reference");
        requireNonNull(submittedAt, "Submission date cannot be null");
    }
}
