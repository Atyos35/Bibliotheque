package com.example.bibliotheque.domain.member;

import java.util.Objects;
import java.util.UUID;

/** Value Object identifiant un {@link Member} de façon unique, encapsulant un UUID. */
public final class MemberId {

    private final UUID value;

    private MemberId(UUID value) {
        this.value = value;
    }

    /** Génère un nouvel identifiant unique, destiné à un membre qui n'existe pas encore. */
    public static MemberId generate() {
        return new MemberId(UUID.randomUUID());
    }

    /**
     * Reconstruit un identifiant à partir de sa représentation textuelle.
     * Lève {@link InvalidMemberIdException} si {@code rawValue} est nul, vide, ou n'est pas un UUID valide.
     */
    public static MemberId of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidMemberIdException("MemberId cannot be null or blank.");
        }
        try {
            return new MemberId(UUID.fromString(rawValue));
        } catch (IllegalArgumentException e) {
            throw new InvalidMemberIdException("MemberId must be a valid UUID: " + rawValue);
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
        if (!(o instanceof MemberId)) {
            return false;
        }
        MemberId memberId = (MemberId) o;
        return value.equals(memberId.value);
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
