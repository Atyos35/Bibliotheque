package com.example.bibliotheque.domain.book;

/**
 * Levée quand on tente d'emprunter un {@link Book} qui n'a plus aucun exemplaire disponible
 * (voir {@link Book#borrowCopy()}).
 */
public class BookNotAvailableException extends RuntimeException {

    public BookNotAvailableException(String message) {
        super(message);
    }
}
