package com.example.bibliotheque.domain.book;

/**
 * Levée quand les quantités d'exemplaires d'un {@link Book} violent l'invariant
 * {@code 0 <= availableCopies <= totalCopies} (à la construction ou lors d'un retour).
 */
public class InvalidBookCopiesException extends RuntimeException {

    public InvalidBookCopiesException(String message) {
        super(message);
    }
}
