package com.example.bibliotheque.domain.loan;

/**
 * Levée lorsqu'une valeur candidate pour {@link LoanId} est nulle, vide, ou n'est pas un UUID valide.
 */
public class InvalidLoanIdException extends RuntimeException {

    public InvalidLoanIdException(String message) {
        super(message);
    }
}
