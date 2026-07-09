package com.example.bibliotheque.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class EmailTest {

    @Test
    void createsEmailFromValidValue() {
        Email email = new Email("jane.doe@example.com");

        assertThat(email.value()).isEqualTo("jane.doe@example.com");
    }

    @Test
    void twoEmailWithTheSameValueAreEqual() {
        assertThat(new Email("jane.doe@example.com")).isEqualTo(new Email("jane.doe@example.com"));
        assertThat(new Email("jane.doe@example.com")).hasSameHashCodeAs(new Email("jane.doe@example.com"));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "",
        "jane.doe",
        "jane.doe@",
        "@example.com",
        "jane.doe@example",
        "jane doe@example.com",
    })
    void rejectsInvalidEmailFormat(String invalidEmail) {
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(InvalidEmailException.class);
    }
}
