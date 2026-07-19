package com.example.bibliotheque.domain.event;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Événement de domaine immuable décrivant un fait passé — émis quand un {@code Loan} est créé
 * pour signaler qu'un exemplaire de livre vient d'être emprunté par un membre.
 */
public record BookBorrowedEvent(
        LoanId loanId,
        BookId bookId,
        MemberId memberId,
        LocalDateTime borrowedAt,
        LocalDateTime dueDate) implements DomainEvent {

    public BookBorrowedEvent {
        Objects.requireNonNull(loanId, "LoanId cannot be null.");
        Objects.requireNonNull(bookId, "BookId cannot be null.");
        Objects.requireNonNull(memberId, "MemberId cannot be null.");
        Objects.requireNonNull(borrowedAt, "borrowedAt cannot be null.");
        Objects.requireNonNull(dueDate, "dueDate cannot be null.");
    }
}
