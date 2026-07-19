package com.example.bibliotheque.domain.loan;

import java.util.Objects;
import java.util.UUID;

/** Value Object identifiant un {@link Loan} de façon unique, encapsulant un UUID. */
public final class LoanId {

    private final UUID value;

    private LoanId(UUID value) {
        this.value = value;
    }

    /** Génère un nouvel identifiant unique, destiné à un emprunt qui n'existe pas encore. */
    public static LoanId generate() {
        return new LoanId(UUID.randomUUID());
    }

    /**
     * Reconstruit un identifiant à partir de sa représentation textuelle.
     * Lève {@link InvalidLoanIdException} si {@code rawValue} est nul, vide, ou n'est pas un UUID valide.
     */
    public static LoanId of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidLoanIdException("LoanId cannot be null or blank.");
        }
        try {
            return new LoanId(UUID.fromString(rawValue));
        } catch (IllegalArgumentException e) {
            throw new InvalidLoanIdException("LoanId must be a valid UUID: " + rawValue);
        }
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoanId)) {
            return false;
        }
        LoanId loanId = (LoanId) o;
        return value.equals(loanId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
