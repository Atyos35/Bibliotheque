package com.example.bibliotheque.domain.book;

/**
 * Levée quand une chaîne fournie à {@link BookId#of(String)} est nulle, vide ou n'est pas
 * un UUID valide.
 */
public class InvalidBookIdException extends RuntimeException {

    public InvalidBookIdException(String message) {
        super(message);
    }
}
