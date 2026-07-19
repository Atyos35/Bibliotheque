package com.example.bibliotheque.infrastructure.book;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.ISBN;

/** Traduit un {@link Book} du domaine vers/depuis sa représentation de persistance {@link BookEntity}. */
public final class BookMapper {

    private BookMapper() {
    }

    public static BookEntity toEntity(Book book) {
        return new BookEntity(book.id().value(), book.isbn().value(), book.title(), book.author(),
                book.totalCopies(), book.availableCopies());
    }

    public static Book toDomain(BookEntity entity) {
        return new Book(BookId.of(entity.getId().toString()), new ISBN(entity.getIsbn()), entity.getTitle(),
                entity.getAuthor(), entity.getTotalCopies(), entity.getAvailableCopies());
    }
}
