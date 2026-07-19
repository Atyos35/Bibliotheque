package com.example.bibliotheque.domain.event;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Événement de domaine immuable décrivant un fait passé — émis quand un {@code Loan} est clôturé
 * pour signaler qu'un exemplaire de livre vient d'être rendu.
 */
public record BookReturnedEvent(
        LoanId loanId,
        BookId bookId,
        MemberId memberId,
        LocalDateTime returnedAt) implements DomainEvent {

    public BookReturnedEvent {
        Objects.requireNonNull(loanId, "LoanId cannot be null.");
        Objects.requireNonNull(bookId, "BookId cannot be null.");
        Objects.requireNonNull(memberId, "MemberId cannot be null.");
        Objects.requireNonNull(returnedAt, "returnedAt cannot be null.");
    }
}
