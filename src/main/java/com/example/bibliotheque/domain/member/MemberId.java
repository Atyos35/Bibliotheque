package com.example.bibliotheque.domain.member;

import java.util.Objects;
import java.util.UUID;

public final class MemberId {

    private final UUID value;

    private MemberId(UUID value) {
        this.value = value;
    }

    public static MemberId generate() {
        return new MemberId(UUID.randomUUID());
    }

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
