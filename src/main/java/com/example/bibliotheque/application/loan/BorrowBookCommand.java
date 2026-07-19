package com.example.bibliotheque.application.loan;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import java.util.Objects;

/** Commande applicative portant les données nécessaires à l'exécution de {@link BorrowBookUseCase}. */
public record BorrowBookCommand(MemberId memberId, BookId bookId, LocalDateTime borrowedAt) {

    public BorrowBookCommand {
        Objects.requireNonNull(memberId, "MemberId cannot be null.");
        Objects.requireNonNull(bookId, "BookId cannot be null.");
        Objects.requireNonNull(borrowedAt, "borrowedAt cannot be null.");
    }
}
