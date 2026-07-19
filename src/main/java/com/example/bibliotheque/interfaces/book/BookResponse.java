package com.example.bibliotheque.interfaces.book;

import com.example.bibliotheque.domain.book.Book;

/**
 * Corps de réponse représentant un livre du catalogue, exposé notamment par
 * {@code GET /api/books}.
 */
public record BookResponse(
        String id,
        String isbn,
        String title,
        String author,
        int totalCopies,
        int availableCopies) {

    /** Construit la réponse HTTP à partir de l'agrégat de domaine {@link Book}. */
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
