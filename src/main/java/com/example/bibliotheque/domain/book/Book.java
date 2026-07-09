package com.example.bibliotheque.domain.book;

import java.util.Objects;

public final class Book {

    private final BookId id;
    private final ISBN isbn;
    private final String title;
    private final String author;
    private final int totalCopies;
    private final int availableCopies;

    public Book(BookId id, ISBN isbn, String title, String author, int totalCopies, int availableCopies) {
        this.id = Objects.requireNonNull(id, "BookId cannot be null.");
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null.");
        this.title = Objects.requireNonNull(title, "title cannot be null.");
        this.author = Objects.requireNonNull(author, "author cannot be null.");
        validateCopies(totalCopies, availableCopies);
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public static Book create(BookId id, ISBN isbn, String title, String author, int totalCopies) {
        return new Book(id, isbn, title, author, totalCopies, totalCopies);
    }

    private static void validateCopies(int totalCopies, int availableCopies) {
        if (totalCopies < 0) {
            throw new InvalidBookCopiesException("totalCopies cannot be negative: " + totalCopies);
        }
        if (availableCopies < 0) {
            throw new InvalidBookCopiesException("availableCopies cannot be negative: " + availableCopies);
        }
        if (availableCopies > totalCopies) {
            throw new InvalidBookCopiesException(
                    "availableCopies (" + availableCopies + ") cannot exceed totalCopies (" + totalCopies + ")."
            );
        }
    }

    public BookId id() {
        return id;
    }

    public ISBN isbn() {
        return isbn;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author;
    }

    public int totalCopies() {
        return totalCopies;
    }

    public int availableCopies() {
        return availableCopies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
