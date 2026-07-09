package com.example.bibliotheque.domain.book;

import java.util.Objects;
import java.util.UUID;

public final class BookId {

    private final UUID value;

    private BookId(UUID value) {
        this.value = value;
    }

    public static BookId generate() {
        return new BookId(UUID.randomUUID());
    }

    public static BookId of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidBookIdException("BookId cannot be null or blank.");
        }
        try {
            return new BookId(UUID.fromString(rawValue));
        } catch (IllegalArgumentException e) {
            throw new InvalidBookIdException("BookId must be a valid UUID: " + rawValue);
        }
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BookId)) {
            return false;
        }
        BookId bookId = (BookId) o;
        return value.equals(bookId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
