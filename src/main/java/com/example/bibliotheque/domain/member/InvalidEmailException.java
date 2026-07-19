package com.example.bibliotheque.domain.member;

/** Levée lorsqu'une valeur candidate pour {@link Email} ne respecte pas le format d'adresse email attendu. */
public class InvalidEmailException extends RuntimeException {

    public InvalidEmailException(String message) {
        super(message);
    }
}
