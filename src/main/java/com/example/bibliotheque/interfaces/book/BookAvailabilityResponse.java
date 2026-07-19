package com.example.bibliotheque.interfaces.book;

/**
 * Corps de réponse de {@code GET /api/books/{bookId}/availability}, indiquant si un exemplaire
 * du livre est actuellement disponible à l'emprunt.
 */
public record BookAvailabilityResponse(boolean available) {
}
