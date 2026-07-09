package com.example.bibliotheque.infrastructure.book;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.BookRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaBookRepository implements BookRepository {

    private final SpringDataBookRepository springDataBookRepository;

    public JpaBookRepository(SpringDataBookRepository springDataBookRepository) {
        this.springDataBookRepository = springDataBookRepository;
    }

    @Override
    public Optional<Book> findById(BookId id) {
        return springDataBookRepository.findById(id.value()).map(BookMapper::toDomain);
    }

    @Override
    public void save(Book book) {
        springDataBookRepository.save(BookMapper.toEntity(book));
    }
}
