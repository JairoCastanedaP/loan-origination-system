package com.co.bank.credit.loan_origination_system.customer.application;

import com.co.bank.credit.loan_origination_system.customer.application.exception.CustomerAlreadyExistsException;
import com.co.bank.credit.loan_origination_system.customer.application.exception.CustomerNotFoundException;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.command.RegisterCustomerCommand;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.model.CustomerView;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.query.FindCustomerByDocumentQuery;
import com.co.bank.credit.loan_origination_system.customer.application.port.in.query.FindCustomerByIdQuery;
import com.co.bank.credit.loan_origination_system.customer.application.service.FindCustomerByDocumentService;
import com.co.bank.credit.loan_origination_system.customer.application.service.FindCustomerByIdService;
import com.co.bank.credit.loan_origination_system.customer.application.service.RegisterCustomerService;
import com.co.bank.credit.loan_origination_system.shared.application.exception.InvalidCommandException;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerServicesTest {

    private final InMemoryCustomerStore store = new InMemoryCustomerStore();
    private final RegisterCustomerService register = new RegisterCustomerService(store);
    private final FindCustomerByIdService findById = new FindCustomerByIdService(store);
    private final FindCustomerByDocumentService findByDocument = new FindCustomerByDocumentService(store);

    private static RegisterCustomerCommand command(String documentNumber, String email) {
        return new RegisterCustomerCommand("CC", documentNumber, " Laura ", "Gómez", email, "+57 300 123 4567",
                new BigDecimal("4500000"));
    }

    @Test
    void registersAndFindsTheCustomer() {
        UUID id = register.register(command("1020304050", "laura@example.com"));

        CustomerView view = findById.findById(new FindCustomerByIdQuery(id));

        assertThat(view.fullName()).isEqualTo("Laura Gómez");
        assertThat(view.phone()).isEqualTo("3001234567");
        assertThat(view.monthlyIncome()).isEqualByComparingTo("4500000");
        assertThat(view.currency()).isEqualTo("COP");
    }

    @Test
    void rf02FindsByDocumentIgnoringSeparators() {
        UUID id = register.register(command("1020304050", "laura@example.com"));

        assertThat(findByDocument.findByDocument(new FindCustomerByDocumentQuery("cc", "1.020.304.050")).id())
                .isEqualTo(id);
    }

    @Test
    void documentMustBeUnique() {
        register.register(command("1020304050", "laura@example.com"));

        assertThatThrownBy(() -> register.register(command("1.020.304.050", "otra@example.com")))
                .isInstanceOf(CustomerAlreadyExistsException.class);
        assertThat(store.count()).isEqualTo(1);
    }

    @Test
    void commandRequiresEveryField() {
        assertThatThrownBy(() -> new RegisterCustomerCommand("CC", "1020304050", "Laura", "Gómez", " ",
                "3001234567", BigDecimal.ONE))
                .isInstanceOf(InvalidCommandException.class).hasMessageContaining("email");
    }

    @Test
    void formatIsValidatedByTheDomain() {
        assertThatThrownBy(() -> register.register(command("1020304050", "no-es-un-correo")))
                .isInstanceOf(ValueObjectException.class);
        assertThat(store.count()).isZero();
    }

    @Test
    void unknownCustomerIsNotFound() {
        assertThatThrownBy(() -> findById.findById(new FindCustomerByIdQuery(UUID.randomUUID())))
                .isInstanceOf(CustomerNotFoundException.class);
        assertThatThrownBy(() -> findByDocument.findByDocument(new FindCustomerByDocumentQuery("CC", "99999999")))
                .isInstanceOf(CustomerNotFoundException.class);
    }
}
