package com.example.bibliotheque.domain.book;

public class InvalidBookCopiesException extends RuntimeException {

    public InvalidBookCopiesException(String message) {
        super(message);
    }
}
