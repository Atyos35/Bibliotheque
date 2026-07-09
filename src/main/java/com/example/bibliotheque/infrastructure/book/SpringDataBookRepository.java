package com.example.bibliotheque.infrastructure.book;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataBookRepository extends JpaRepository<BookEntity, UUID> {
}
