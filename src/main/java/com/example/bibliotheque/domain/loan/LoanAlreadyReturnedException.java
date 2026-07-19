package com.example.bibliotheque.domain.loan;

/**
 * Levée lorsqu'on tente de retourner un emprunt déjà clôturé (un {@link Loan} dont {@code returnedAt}
 * est déjà renseigné).
 */
public class LoanAlreadyReturnedException extends RuntimeException {

    public LoanAlreadyReturnedException(String message) {
        super(message);
    }
}
