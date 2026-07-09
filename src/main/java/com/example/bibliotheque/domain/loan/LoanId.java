package com.example.bibliotheque.domain.loan;

import java.util.Objects;
import java.util.UUID;

public final class LoanId {

    private final UUID value;

    private LoanId(UUID value) {
        this.value = value;
    }

    public static LoanId generate() {
        return new LoanId(UUID.randomUUID());
    }

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
