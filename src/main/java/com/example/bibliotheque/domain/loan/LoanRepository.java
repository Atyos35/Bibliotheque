package com.example.bibliotheque.domain.loan;

import com.example.bibliotheque.domain.member.MemberId;
import java.util.List;
import java.util.Optional;

/**
 * Port du domaine pour la persistance des {@link Loan}. L'implémentation concrète vit dans la couche
 * infrastructure ; le domaine ne connaît que cette interface.
 */
public interface LoanRepository {

    Optional<Loan> findById(LoanId id);

    /**
     * Retourne tous les emprunts (actifs ou non) d'un membre, utilisé notamment pour vérifier
     * les invariants de {@link Loan#borrow}.
     */
    List<Loan> findByMemberId(MemberId memberId);

    void save(Loan loan);
}
