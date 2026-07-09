package com.example.bibliotheque.domain.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class IsbnTest {

    private static final String VALID_ISBN_13 = "9780134685991";

    @Test
    void createsIsbnFromValidThirteenDigitValue() {
        ISBN isbn = new ISBN(VALID_ISBN_13);

        assertThat(isbn.value()).isEqualTo(VALID_ISBN_13);
    }

    @Test
    void normalizesHyphensAndSpacesBeforeValidating() {
        ISBN isbn = new ISBN("978-0-13-468599-1");

        assertThat(isbn.value()).isEqualTo(VALID_ISBN_13);
    }

    @Test
    void twoIsbnWithTheSameValueAreEqual() {
        assertThat(new ISBN(VALID_ISBN_13)).isEqualTo(new ISBN(VALID_ISBN_13));
        assertThat(new ISBN(VALID_ISBN_13)).hasSameHashCodeAs(new ISBN(VALID_ISBN_13));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {
        "",
        "978013468599",
        "97801346859912",
        "978013468599X",
        "9780134685992",
    })
    void rejectsInvalidIsbnFormat(String invalidIsbn) {
        assertThatThrownBy(() -> new ISBN(invalidIsbn))
                .isInstanceOf(InvalidIsbnException.class);
    }
}
