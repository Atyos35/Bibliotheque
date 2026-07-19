package com.example.bibliotheque.infrastructure.book;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository Spring Data JPA technique pour {@link BookEntity}, utilisé en interne par {@link JpaBookRepository}. */
public interface SpringDataBookRepository extends JpaRepository<BookEntity, UUID> {
}
