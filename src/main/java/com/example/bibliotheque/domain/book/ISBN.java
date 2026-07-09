package com.example.bibliotheque.domain.book;

public final class ISBN {

    private static final int ISBN_13_LENGTH = 13;

    private final String value;

    public ISBN(String rawValue) {
        String normalized = normalize(rawValue);
        validate(normalized);
        this.value = normalized;
    }

    private static String normalize(String rawValue) {
        if (rawValue == null) {
            throw new InvalidIsbnException("ISBN cannot be null.");
        }
        return rawValue.replaceAll("[\\s-]", "");
    }

    private static void validate(String normalized) {
        if (normalized.length() != ISBN_13_LENGTH || !isOnlyDigits(normalized)) {
            throw new InvalidIsbnException("ISBN must be 13 digits (ISBN-13 format): " + normalized);
        }
        if (!hasValidChecksum(normalized)) {
            throw new InvalidIsbnException("ISBN-13 checksum is invalid: " + normalized);
        }
    }

    private static boolean isOnlyDigits(String value) {
        return value.chars().allMatch(Character::isDigit);
    }

    private static boolean hasValidChecksum(String digits) {
        int sum = 0;
        for (int i = 0; i < ISBN_13_LENGTH; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += digit * (i % 2 == 0 ? 1 : 3);
        }
        return sum % 10 == 0;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ISBN)) {
            return false;
        }
        ISBN isbn = (ISBN) o;
        return value.equals(isbn.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
