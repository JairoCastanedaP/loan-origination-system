package com.co.bank.credit.loan_origination_system.loanapplication.domain;

import java.time.Instant;

import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.shared.domain.utils.Validate.requireNonNull;

/**
 * Decisión final sobre la solicitud, tomada por el analista o por el comité.
 *
 * @param followsRecommendation si coincide con la recomendación automática
 * @param justification         motivo del rechazo o justificación por apartarse de la recomendación;
 *                              puede ser nulo en una aprobación que sigue la recomendación
 */
public record Decision(
        boolean approved,
        String decidedBy,
        boolean byCommittee,
        boolean followsRecommendation,
        String justification,
        Instant decidedAt) {

    public Decision {
        decidedBy = requireNonBlank(decidedBy, "Decision maker cannot be null or blank");
        justification = justification == null || justification.isBlank() ? null : justification.trim();
        requireNonNull(decidedAt, "Decision date cannot be null");
    }
}
