package com.example.bibliotheque.domain.member;

public class InvalidMemberIdException extends RuntimeException {

    public InvalidMemberIdException(String message) {
        super(message);
    }
}
