package com.example.bibliotheque.domain.member;

import java.util.regex.Pattern;

public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(String rawValue) {
        validate(rawValue);
        this.value = rawValue;
    }

    private static void validate(String rawValue) {
        if (rawValue == null || !EMAIL_PATTERN.matcher(rawValue).matches()) {
            throw new InvalidEmailException("Email format is invalid: " + rawValue);
        }
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Email)) {
            return false;
        }
        Email email = (Email) o;
        return value.equals(email.value);
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
