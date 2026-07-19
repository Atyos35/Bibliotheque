package com.example.bibliotheque.domain.loan;

/**
 * Levée lors d'une tentative d'emprunt lorsque le membre a déjà atteint le nombre maximal
 * d'emprunts actifs simultanés autorisé ({@link Loan#MAX_ACTIVE_LOANS_PER_MEMBER}).
 */
public class LoanLimitExceededException extends RuntimeException {

    public LoanLimitExceededException(String message) {
        super(message);
    }
}
