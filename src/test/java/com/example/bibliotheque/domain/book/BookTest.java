package com.example.bibliotheque.domain.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class BookTest {

    private static final ISBN VALID_ISBN = new ISBN("9780134685991");

    @Test
    void createsBookWithAllCopiesAvailable() {
        Book book = Book.create(BookId.generate(), VALID_ISBN, "Effective Java", "Joshua Bloch", 5);

        assertThat(book.totalCopies()).isEqualTo(5);
        assertThat(book.availableCopies()).isEqualTo(5);
        assertThat(book.title()).isEqualTo("Effective Java");
        assertThat(book.author()).isEqualTo("Joshua Bloch");
        assertThat(book.isbn()).isEqualTo(VALID_ISBN);
    }

    @Test
    void createsBookWithSomeCopiesAlreadyBorrowed() {
        Book book = new Book(BookId.generate(), VALID_ISBN, "Effective Java", "Joshua Bloch", 5, 2);

        assertThat(book.totalCopies()).isEqualTo(5);
        assertThat(book.availableCopies()).isEqualTo(2);
    }

    @Test
    void rejectsAvailableCopiesGreaterThanTotalCopies() {
        assertThatThrownBy(() ->
                new Book(BookId.generate(), VALID_ISBN, "Effective Java", "Joshua Bloch", 3, 4))
                .isInstanceOf(InvalidBookCopiesException.class);
    }

    @Test
    void rejectsNegativeAvailableCopies() {
        assertThatThrownBy(() ->
                new Book(BookId.generate(), VALID_ISBN, "Effective Java", "Joshua Bloch", 3, -1))
                .isInstanceOf(InvalidBookCopiesException.class);
    }

    @Test
    void rejectsNegativeTotalCopies() {
        assertThatThrownBy(() ->
                new Book(BookId.generate(), VALID_ISBN, "Effective Java", "Joshua Bloch", -1, 0))
                .isInstanceOf(InvalidBookCopiesException.class);
    }

    @Test
    void rejectsNullIsbn() {
        assertThatThrownBy(() ->
                new Book(BookId.generate(), null, "Effective Java", "Joshua Bloch", 3, 3))
                .isInstanceOf(NullPointerException.class);
    }
}
