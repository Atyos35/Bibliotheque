package com.example.bibliotheque.application.loan;

/** Levée quand un livre référencé par son identifiant est introuvable dans le dépôt. */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String message) {
        super(message);
    }
}
