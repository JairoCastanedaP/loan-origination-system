package com.co.bank.credit.loan_origination_system.shared.domain.kernel;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AggregateRootTest {

    private record SampleEvent(UUID eventId, Instant occurredOn) implements DomainEvent {
        SampleEvent() {
            this(UUID.randomUUID(), Instant.now());
        }

        @Override
        public String eventType() {
            return "sample-event";
        }
    }

    private static final class SampleAggregate extends AggregateRoot<UUID> {
        SampleAggregate(UUID id) {
            super(id);
        }

        void doSomething() {
            addEvent(new SampleEvent());
        }
    }

    @Test
    void pullDomainEventsReturnsEventsOnlyOnce() {
        SampleAggregate aggregate = new SampleAggregate(UUID.randomUUID());
        aggregate.doSomething();
        aggregate.doSomething();

        assertThat(aggregate.hasDomainEvents()).isTrue();
        assertThat(aggregate.pullDomainEvents()).hasSize(2);
        assertThat(aggregate.pullDomainEvents()).isEmpty();
        assertThat(aggregate.hasDomainEvents()).isFalse();
    }

    @Test
    void pulledEventsCannotBeModified() {
        SampleAggregate aggregate = new SampleAggregate(UUID.randomUUID());
        aggregate.doSomething();

        assertThatThrownBy(() -> aggregate.pullDomainEvents().clear())
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void equalityIsByIdentity() {
        UUID id = UUID.randomUUID();

        assertThat(new SampleAggregate(id)).isEqualTo(new SampleAggregate(id));
        assertThat(new SampleAggregate(id)).isNotEqualTo(new SampleAggregate(UUID.randomUUID()));
    }

    @Test
    void requiresAnId() {
        assertThatThrownBy(() -> new SampleAggregate(null)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void eventsExposeDefaultMetadata() {
        DomainEvent event = new SampleEvent();

        assertThat(event.systemName()).isEqualTo("Loan Origination System");
        assertThat(event.version()).isEqualTo("v1");
    }
}
