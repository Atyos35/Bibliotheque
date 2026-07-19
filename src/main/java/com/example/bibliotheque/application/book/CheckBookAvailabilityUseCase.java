package com.example.bibliotheque.application.book;

import com.example.bibliotheque.application.loan.BookNotFoundException;
import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.BookRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Use case qui orchestre la consultation de la disponibilité d'un livre, aucune règle métier ici :
 * la décision revient entièrement à {@link Book#isAvailable()}.
 */
@Service
public final class CheckBookAvailabilityUseCase {

    private final BookRepository bookRepository;

    public CheckBookAvailabilityUseCase(BookRepository bookRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null.");
    }

    /** Recherche le livre par son identifiant et indique s'il a au moins un exemplaire disponible. */
    public boolean execute(BookId bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + bookId));
        return book.isAvailable();
    }
}
