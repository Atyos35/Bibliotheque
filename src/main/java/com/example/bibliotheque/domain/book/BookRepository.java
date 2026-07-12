package com.example.bibliotheque.domain.book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Optional<Book> findById(BookId id);

    List<Book> findAll();

    void save(Book book);
}
