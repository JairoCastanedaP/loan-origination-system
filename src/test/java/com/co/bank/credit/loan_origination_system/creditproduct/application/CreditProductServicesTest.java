package com.co.bank.credit.loan_origination_system.creditproduct.application;

import com.co.bank.credit.loan_origination_system.creditproduct.application.exception.CreditProductNotFoundException;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.command.CreateCreditProductCommand;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.model.CreditProductView;
import com.co.bank.credit.loan_origination_system.creditproduct.application.port.in.query.FindCreditProductByIdQuery;
import com.co.bank.credit.loan_origination_system.creditproduct.application.service.CreateCreditProductService;
import com.co.bank.credit.loan_origination_system.creditproduct.application.service.FindCreditProductByIdService;
import com.co.bank.credit.loan_origination_system.shared.application.exception.InvalidCommandException;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.EntityException;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreditProductServicesTest {

    private final InMemoryCreditProductStore store = new InMemoryCreditProductStore();
    private final CreateCreditProductService create = new CreateCreditProductService(store);
    private final FindCreditProductByIdService findById = new FindCreditProductByIdService(store);

    private static CreateCreditProductCommand vehicle(String type, Set<String> requirements) {
        return new CreateCreditProductCommand("Crédito de vehículo", type, new BigDecimal("19.5"),
                new BigDecimal("5000000"), new BigDecimal("150000000"), 12, 72, new BigDecimal("120000"),
                requirements);
    }

    @Test
    void createsAnActiveProduct() {
        UUID id = create.create(vehicle("vehicle_loan", Set.of("IDENTITY_DOCUMENT", "vehicle_quote")));

        CreditProductView view = findById.findById(new FindCreditProductByIdQuery(id));

        assertThat(view.type()).isEqualTo("VEHICLE_LOAN");
        assertThat(view.annualInterestRate()).isEqualByComparingTo("19.5");
        assertThat(view.maxTermMonths()).isEqualTo(72);
        assertThat(view.requiredRequirements()).containsExactlyInAnyOrder("IDENTITY_DOCUMENT", "VEHICLE_QUOTE");
        assertThat(view.active()).isTrue();
    }

    @Test
    void rejectsUnknownTypesAndRequirements() {
        assertThatThrownBy(() -> create.create(vehicle("MORTGAGE", Set.of("IDENTITY_DOCUMENT"))))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> create.create(vehicle("VEHICLE_LOAN", Set.of("SELFIE"))))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void productNeedsAtLeastOneRequirement() {
        assertThatThrownBy(() -> create.create(vehicle("VEHICLE_LOAN", Set.of())))
                .isInstanceOf(EntityException.class);
    }

    @Test
    void commandRequiresEveryField() {
        assertThatThrownBy(() -> new CreateCreditProductCommand("Vehículo", "VEHICLE_LOAN", null, BigDecimal.ONE,
                BigDecimal.TEN, 12, 72, BigDecimal.ONE, Set.of("IDENTITY_DOCUMENT")))
                .isInstanceOf(InvalidCommandException.class).hasMessageContaining("annualInterestRate");
    }

    @Test
    void unknownProductIsNotFound() {
        assertThatThrownBy(() -> findById.findById(new FindCreditProductByIdQuery(UUID.randomUUID())))
                .isInstanceOf(CreditProductNotFoundException.class);
    }
}
