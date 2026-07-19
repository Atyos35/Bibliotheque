package com.example.bibliotheque.application.loan;

/** Levée quand un membre référencé par son identifiant est introuvable dans le dépôt. */
public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super(message);
    }
}
