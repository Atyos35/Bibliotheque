package com.example.bibliotheque.interfaces.book;

import com.example.bibliotheque.application.book.CheckBookAvailabilityUseCase;
import com.example.bibliotheque.domain.book.BookId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final CheckBookAvailabilityUseCase checkBookAvailabilityUseCase;

    public BookController(CheckBookAvailabilityUseCase checkBookAvailabilityUseCase) {
        this.checkBookAvailabilityUseCase = checkBookAvailabilityUseCase;
    }

    @GetMapping("/{bookId}/availability")
    public ResponseEntity<BookAvailabilityResponse> availability(@PathVariable String bookId) {
        boolean available = checkBookAvailabilityUseCase.execute(BookId.of(bookId));
        return ResponseEntity.ok(new BookAvailabilityResponse(available));
    }
}
