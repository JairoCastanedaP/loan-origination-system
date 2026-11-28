package com.co.bank.credit.loan_origination_system.shared.application.port.out;

import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;

import java.util.List;

/**
 * Publica los eventos de dominio después de guardar el agregado.
 * En la fase 07 lo implementa el adaptador de RabbitMQ.
 */
public interface PublishDomainEventsPort {

    void publish(DomainEvent event);

    default void publishAll(List<DomainEvent> events) {
        events.forEach(this::publish);
    }
}
