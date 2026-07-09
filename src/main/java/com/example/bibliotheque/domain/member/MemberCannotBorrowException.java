package com.example.bibliotheque.domain.member;

public class MemberCannotBorrowException extends RuntimeException {

    public MemberCannotBorrowException(String message) {
        super(message);
    }
}
