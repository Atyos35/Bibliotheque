package com.example.bibliotheque.interfaces.book;

import com.example.bibliotheque.application.book.CheckBookAvailabilityUseCase;
import com.example.bibliotheque.application.book.ListBooksUseCase;
import com.example.bibliotheque.domain.book.BookId;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST exposant la consultation du catalogue de livres. Traduit les requêtes HTTP
 * en appels aux cas d'usage applicatifs ; aucune règle métier n'est décidée ici.
 */
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final CheckBookAvailabilityUseCase checkBookAvailabilityUseCase;
    private final ListBooksUseCase listBooksUseCase;

    public BookController(CheckBookAvailabilityUseCase checkBookAvailabilityUseCase,
                           ListBooksUseCase listBooksUseCase) {
        this.checkBookAvailabilityUseCase = checkBookAvailabilityUseCase;
        this.listBooksUseCase = listBooksUseCase;
    }

    /** Liste l'ensemble des livres du catalogue. */
    @GetMapping
    public ResponseEntity<List<BookResponse>> list() {
        List<BookResponse> books = listBooksUseCase.execute().stream()
                .map(BookResponse::from)
                .toList();
        return ResponseEntity.ok(books);
    }

    /** Indique si au moins un exemplaire du livre désigné est actuellement disponible à l'emprunt. */
    @GetMapping("/{bookId}/availability")
    public ResponseEntity<BookAvailabilityResponse> availability(@PathVariable String bookId) {
        boolean available = checkBookAvailabilityUseCase.execute(BookId.of(bookId));
        return ResponseEntity.ok(new BookAvailabilityResponse(available));
    }
}
