package com.example.bibliotheque.domain.loan;

public class LoanAlreadyReturnedException extends RuntimeException {

    public LoanAlreadyReturnedException(String message) {
        super(message);
    }
}
