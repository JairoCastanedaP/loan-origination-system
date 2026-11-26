package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import com.co.bank.credit.loan_origination_system.creditproduct.domain.RequirementType;

import java.time.Instant;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

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
