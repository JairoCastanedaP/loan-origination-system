package com.co.bank.credit.loan_origination_system.shared.application;

import com.co.bank.credit.loan_origination_system.shared.application.port.out.PublishDomainEventsPort;
import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Doble de prueba: guarda los eventos publicados para poder verificarlos.
 */
public class RecordingEventPublisher implements PublishDomainEventsPort {

    private final List<DomainEvent> published = new ArrayList<>();

    @Override
    public void publish(DomainEvent event) {
        published.add(event);
    }

    public List<DomainEvent> published() {
        return List.copyOf(published);
    }

    public <T extends DomainEvent> List<T> published(Class<T> type) {
        return published.stream().filter(type::isInstance).map(type::cast).toList();
    }

    public void clear() {
        published.clear();
    }
}
