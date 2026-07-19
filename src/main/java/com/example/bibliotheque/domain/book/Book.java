package com.example.bibliotheque.domain.book;

import java.util.Objects;

/**
 * Agrégat racine représentant un livre du catalogue. Protège l'invariant selon lequel
 * {@code availableCopies} ne peut jamais être négatif ni dépasser {@code totalCopies}.
 */
public final class Book {

    private final BookId id;
    private final ISBN isbn;
    private final String title;
    private final String author;
    private final int totalCopies;
    private int availableCopies;

    /**
     * Reconstitue un livre existant (depuis la persistance, ou avec un nombre d'exemplaires
     * disponibles différent du total). Lève {@link InvalidBookCopiesException} si les
     * quantités violent l'invariant.
     */
    public Book(BookId id, ISBN isbn, String title, String author, int totalCopies, int availableCopies) {
        this.id = Objects.requireNonNull(id, "BookId cannot be null.");
        this.isbn = Objects.requireNonNull(isbn, "ISBN cannot be null.");
        this.title = Objects.requireNonNull(title, "title cannot be null.");
        this.author = Objects.requireNonNull(author, "author cannot be null.");
        validateCopies(totalCopies, availableCopies);
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    /** Crée un nouveau livre dont tous les exemplaires sont initialement disponibles. */
    public static Book create(BookId id, ISBN isbn, String title, String author, int totalCopies) {
        return new Book(id, isbn, title, author, totalCopies, totalCopies);
    }

    /** Indique si au moins un exemplaire est actuellement disponible à l'emprunt. */
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    /**
     * Décrémente le nombre d'exemplaires disponibles lors d'un emprunt.
     * Lève {@link BookNotAvailableException} si aucun exemplaire n'est disponible.
     */
    public void borrowCopy() {
        if (availableCopies <= 0) {
            throw new BookNotAvailableException("No available copy for book " + id + ".");
        }
        availableCopies--;
    }

    /**
     * Incrémente le nombre d'exemplaires disponibles lors d'un retour.
     * Lève {@link InvalidBookCopiesException} si l'incrément dépasserait le nombre total
     * d'exemplaires (signe d'une incohérence appelante, pas d'un cas métier normal).
     */
    public void returnCopy() {
        if (availableCopies >= totalCopies) {
            throw new InvalidBookCopiesException(
                    "availableCopies cannot exceed totalCopies (" + totalCopies + ") for book " + id + ".");
        }
        availableCopies++;
    }

    private static void validateCopies(int totalCopies, int availableCopies) {
        if (totalCopies < 0) {
            throw new InvalidBookCopiesException("totalCopies cannot be negative: " + totalCopies);
        }
        if (availableCopies < 0) {
            throw new InvalidBookCopiesException("availableCopies cannot be negative: " + availableCopies);
        }
        if (availableCopies > totalCopies) {
            throw new InvalidBookCopiesException(
                    "availableCopies (" + availableCopies + ") cannot exceed totalCopies (" + totalCopies + ")."
            );
        }
    }

    public BookId id() {
        return id;
    }

    public ISBN isbn() {
        return isbn;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author;
    }

    public int totalCopies() {
        return totalCopies;
    }

    public int availableCopies() {
        return availableCopies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Book)) {
            return false;
        }
        Book book = (Book) o;
        return id.equals(book.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
