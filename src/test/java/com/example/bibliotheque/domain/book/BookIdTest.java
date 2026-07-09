package com.example.bibliotheque.domain.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookIdTest {

    @Test
    void generatesUniqueBookIds() {
        BookId first = BookId.generate();
        BookId second = BookId.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void createsBookIdFromValidUuidString() {
        UUID uuid = UUID.randomUUID();

        BookId bookId = BookId.of(uuid.toString());

        assertThat(bookId.value()).isEqualTo(uuid);
    }

    @Test
    void twoBookIdWithTheSameValueAreEqual() {
        UUID uuid = UUID.randomUUID();

        assertThat(BookId.of(uuid.toString())).isEqualTo(BookId.of(uuid.toString()));
    }

    @Test
    void rejectsNullBookId() {
        assertThatThrownBy(() -> BookId.of(null))
                .isInstanceOf(InvalidBookIdException.class);
    }

    @Test
    void rejectsBlankBookId() {
        assertThatThrownBy(() -> BookId.of("   "))
                .isInstanceOf(InvalidBookIdException.class);
    }

    @Test
    void rejectsNonUuidBookId() {
        assertThatThrownBy(() -> BookId.of("not-a-uuid"))
                .isInstanceOf(InvalidBookIdException.class);
    }
}
