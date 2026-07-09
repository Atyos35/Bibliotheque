package com.example.bibliotheque.domain.loan;

public class InvalidLoanIdException extends RuntimeException {

    public InvalidLoanIdException(String message) {
        super(message);
    }
}
