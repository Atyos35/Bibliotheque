package com.example.bibliotheque.domain.loan;

public class LoanLimitExceededException extends RuntimeException {

    public LoanLimitExceededException(String message) {
        super(message);
    }
}
