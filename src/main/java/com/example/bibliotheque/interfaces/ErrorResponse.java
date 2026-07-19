package com.example.bibliotheque.interfaces;

/**
 * Corps de réponse HTTP renvoyé par {@link DomainExceptionHandler} pour toute erreur traduite
 * depuis une exception de domaine ou d'application.
 */
public record ErrorResponse(String message) {
}
