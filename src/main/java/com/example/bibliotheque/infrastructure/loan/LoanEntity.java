package com.example.bibliotheque.infrastructure.loan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité JPA de persistance de l'emprunt. Simple structure de données pour Hibernate : ne porte
 * aucune règle métier, celle-ci reste dans l'agrégat {@link com.example.bibliotheque.domain.loan.Loan}.
 */
@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private UUID memberId;

    @Column(nullable = false)
    private LocalDateTime borrowedAt;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime returnedAt;

    protected LoanEntity() {
    }

    public LoanEntity(UUID id, UUID bookId, UUID memberId, LocalDateTime borrowedAt, LocalDateTime dueDate,
                       LocalDateTime returnedAt) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowedAt = borrowedAt;
        this.dueDate = dueDate;
        this.returnedAt = returnedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getBookId() {
        return bookId;
    }

    public UUID getMemberId() {
        return memberId;
    }

    public LocalDateTime getBorrowedAt() {
        return borrowedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public LocalDateTime getReturnedAt() {
        return returnedAt;
    }
}
