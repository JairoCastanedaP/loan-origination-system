package com.co.bank.credit.loan_origination_system.domain.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.co.bank.credit.loan_origination_system.domain.commons.Validate.requireNonNull;

/**
 * Raíz de agregado: entidad con identidad propia que registra los eventos de dominio
 * que produce. Dos agregados son iguales si tienen la misma identidad.
 *
 * @param <ID> tipo del identificador (un Value Object)
 */
public abstract class AggregateRoot<ID> {

    private final ID id;
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected AggregateRoot(ID id) {
        this.id = requireNonNull(id, "Aggregate id cannot be null");
    }

    public ID getId() {
        return id;
    }

    protected void addEvent(DomainEvent event) {
        domainEvents.add(requireNonNull(event, "Domain event cannot be null"));
    }

    /**
     * Devuelve los eventos pendientes y los elimina del agregado,
     * para que se publiquen una sola vez.
     */
    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public boolean hasDomainEvents() {
        return !domainEvents.isEmpty();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((AggregateRoot<?>) o).id);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getClass(), id);
    }
}
