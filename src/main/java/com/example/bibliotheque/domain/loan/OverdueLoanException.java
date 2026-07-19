package com.example.bibliotheque.domain.loan;

/**
 * Levée lors d'une tentative d'emprunt lorsque le membre a déjà un emprunt en retard
 * (date d'échéance dépassée sans retour) : aucun nouvel emprunt n'est autorisé tant que
 * ce retard n'est pas régularisé.
 */
public class OverdueLoanException extends RuntimeException {

    public OverdueLoanException(String message) {
        super(message);
    }
}
