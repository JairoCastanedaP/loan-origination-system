package com.co.bank.credit.loan_origination_system.shared.domain.model;

import com.co.bank.credit.loan_origination_system.shared.domain.exception.ValueObjectException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DocumentNumberTest {

    @ParameterizedTest
    @CsvSource({
            "CC, 1.020.304.050, 1020304050",
            "cc, 123456, 123456",
            "CE, 'e-123456', E123456",
            "PA, ab 123456, AB123456"
    })
    void normalizesValidDocuments(String type, String number, String expected) {
        DocumentNumber document = DocumentNumber.of(type, number);

        assertThat(document.getNumber()).isEqualTo(expected);
        assertThat(document.getType()).isEqualTo(DocumentType.of(type));
    }

    @ParameterizedTest
    @CsvSource({
            "CC, 12345",
            "CC, 12345678901",
            "CC, 12A456",
            "PA, 12345",
            "XX, 123456"
    })
    void rejectsInvalidDocuments(String type, String number) {
        assertThatThrownBy(() -> DocumentNumber.of(type, number)).isInstanceOf(ValueObjectException.class);
    }

    @Test
    void rejectsNullValues() {
        assertThatThrownBy(() -> DocumentNumber.of((DocumentType) null, "123456"))
                .isInstanceOf(ValueObjectException.class);
        assertThatThrownBy(() -> DocumentNumber.of(DocumentType.CC, null))
                .isInstanceOf(ValueObjectException.class);
    }

    @Test
    void equalWhenSameTypeAndNumber() {
        assertThat(DocumentNumber.of("CC", "1.020.304")).isEqualTo(DocumentNumber.of(DocumentType.CC, "1020304"));
        assertThat(DocumentNumber.of("CC", "1020304")).isNotEqualTo(DocumentNumber.of("CE", "1020304"));
        assertThat(DocumentNumber.of("CC", "1020304")).hasToString("CC-1020304");
    }
}
