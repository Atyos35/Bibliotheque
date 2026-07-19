package com.example.bibliotheque.domain.member;

/**
 * Levée lorsqu'une valeur candidate pour {@link MemberId} est nulle, vide, ou n'est pas un UUID valide.
 */
public class InvalidMemberIdException extends RuntimeException {

    public InvalidMemberIdException(String message) {
        super(message);
    }
}
