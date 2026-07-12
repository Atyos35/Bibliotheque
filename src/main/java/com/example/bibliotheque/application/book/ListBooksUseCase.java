package com.example.bibliotheque.application.book;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public final class ListBooksUseCase {

    private final BookRepository bookRepository;

    public ListBooksUseCase(BookRepository bookRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null.");
    }

    public List<Book> execute() {
        return bookRepository.findAll();
    }
}
