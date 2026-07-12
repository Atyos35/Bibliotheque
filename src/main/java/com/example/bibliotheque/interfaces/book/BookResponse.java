package com.example.bibliotheque.interfaces.book;

import com.example.bibliotheque.domain.book.Book;

public record BookResponse(
        String id,
        String isbn,
        String title,
        String author,
        int totalCopies,
        int availableCopies) {

    public static BookResponse from(Book book) {
        return new BookResponse(
                book.id().toString(),
                book.isbn().value(),
                book.title(),
                book.author(),
                book.totalCopies(),
                book.availableCopies());
    }
}
