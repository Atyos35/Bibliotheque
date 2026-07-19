package com.example.bibliotheque.domain.book;

/**
 * Levée quand la valeur fournie à {@link ISBN} n'est pas un ISBN-13 valide (mauvaise longueur,
 * caractères non numériques, ou checksum incorrect).
 */
public class InvalidIsbnException extends RuntimeException {

    public InvalidIsbnException(String message) {
        super(message);
    }
}
