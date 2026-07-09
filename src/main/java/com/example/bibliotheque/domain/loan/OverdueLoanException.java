package com.example.bibliotheque.domain.loan;

public class OverdueLoanException extends RuntimeException {

    public OverdueLoanException(String message) {
        super(message);
    }
}
