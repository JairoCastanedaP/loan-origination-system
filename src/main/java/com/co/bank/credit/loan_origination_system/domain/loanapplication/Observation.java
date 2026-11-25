package com.co.bank.credit.loan_origination_system.domain.loanapplication;

import com.co.bank.credit.loan_origination_system.domain.exceptions.BusinessRuleException;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireLength;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonBlank;
import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Observación del analista sobre una solicitud. Vive dentro del agregado
 * {@link LoanApplication}: solo se crea y se resuelve a través de él.
 */
public final class Observation {

    private final UUID id;
    private final String description;
    private final Instant createdAt;
    private final Instant dueAt;
    private Instant resolvedAt;

    private Observation(UUID id, String description, Instant createdAt, Instant dueAt, Instant resolvedAt) {
        this.id = requireNonNull(id, "Observation id cannot be null");
        this.description = requireLength(
                requireNonBlank(description, "Observation description cannot be null or blank"),
                5, 500, "Observation description");
        this.createdAt = requireNonNull(createdAt, "Observation date cannot be null");
        this.dueAt = requireNonNull(dueAt, "Observation due date cannot be null");
        if (!dueAt.isAfter(createdAt)) {
            throw new BusinessRuleException("RN-08", "Observation due date must be after its creation date");
        }
        this.resolvedAt = resolvedAt;
    }

    static Observation create(String description, Instant createdAt, Instant dueAt) {
        return new Observation(UUID.randomUUID(), description, createdAt, dueAt, null);
    }

    public static Observation reconstitute(UUID id, String description, Instant createdAt, Instant dueAt,
                                           Instant resolvedAt) {
        return new Observation(id, description, createdAt, dueAt, resolvedAt);
    }

    void resolve(Instant now) {
        this.resolvedAt = requireNonNull(now, "Resolution date cannot be null");
    }

    public boolean isPending() {
        return resolvedAt == null;
    }

    public boolean isOverdue(Instant now) {
        return isPending() && now.isAfter(dueAt);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getDueAt() { return dueAt; }
    public Instant getResolvedAt() { return resolvedAt; }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Observation that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
