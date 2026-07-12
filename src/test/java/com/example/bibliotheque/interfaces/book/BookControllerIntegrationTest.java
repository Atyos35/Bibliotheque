package com.example.bibliotheque.interfaces.book;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.ISBN;
import com.example.bibliotheque.infrastructure.book.BookMapper;
import com.example.bibliotheque.infrastructure.book.SpringDataBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@Transactional
class BookControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SpringDataBookRepository springDataBookRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private BookId givenABookWithAvailableCopies() {
        BookId bookId = BookId.generate();
        Book book = Book.create(bookId, new ISBN("9780134685991"), "Effective Java", "Joshua Bloch", 1);
        springDataBookRepository.save(BookMapper.toEntity(book));
        return bookId;
    }

    private BookId givenABookWithNoAvailableCopy() {
        BookId bookId = BookId.generate();
        Book book = new Book(bookId, new ISBN("9780134685991"), "Effective Java", "Joshua Bloch", 1, 0);
        springDataBookRepository.save(BookMapper.toEntity(book));
        return bookId;
    }

    @Test
    void availabilityIsTrueWhenBookHasAvailableCopies() throws Exception {
        BookId bookId = givenABookWithAvailableCopies();

        mockMvc.perform(get("/api/books/" + bookId + "/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void availabilityIsFalseWhenBookHasNoAvailableCopy() throws Exception {
        BookId bookId = givenABookWithNoAvailableCopy();

        mockMvc.perform(get("/api/books/" + bookId + "/availability"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void availabilityReturnsNotFoundWhenBookDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/books/" + BookId.generate() + "/availability"))
                .andExpect(status().isNotFound());
    }
}
