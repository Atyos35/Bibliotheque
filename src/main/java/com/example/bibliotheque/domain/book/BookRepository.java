package com.example.bibliotheque.domain.book;

import java.util.Optional;

public interface BookRepository {

    Optional<Book> findById(BookId id);

    void save(Book book);
}
