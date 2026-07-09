package com.example.bibliotheque.domain.book;

public class InvalidBookIdException extends RuntimeException {

    public InvalidBookIdException(String message) {
        super(message);
    }
}
