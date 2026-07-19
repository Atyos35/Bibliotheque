package com.example.bibliotheque.interfaces.loan;

/**
 * Corps de requête de {@code POST /api/loans}, identifiant le membre emprunteur et le livre
 * à emprunter.
 */
public record BorrowBookRequest(String memberId, String bookId) {
}
