package com.example.bibliotheque.application.loan;

/** Levée quand un emprunt référencé par son identifiant est introuvable dans le dépôt. */
public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(String message) {
        super(message);
    }
}
