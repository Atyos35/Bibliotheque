package com.example.bibliotheque.domain.member;

/**
 * Levée lorsqu'un emprunt est demandé pour un membre dont {@link Member#canBorrow()} renvoie {@code false}
 * (statut d'adhésion autre que {@link MembershipStatus#ACTIVE}).
 */
public class MemberCannotBorrowException extends RuntimeException {

    public MemberCannotBorrowException(String message) {
        super(message);
    }
}
