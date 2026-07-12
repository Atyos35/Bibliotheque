package com.example.bibliotheque.application.book;

import com.example.bibliotheque.application.loan.BookNotFoundException;
import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.BookRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public final class CheckBookAvailabilityUseCase {

    private final BookRepository bookRepository;

    public CheckBookAvailabilityUseCase(BookRepository bookRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null.");
    }

    public boolean execute(BookId bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));
        return book.isAvailable();
    }
}
