package com.example.bibliotheque.application.loan;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookRepository;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanRepository;
import java.util.Objects;

public final class ReturnBookUseCase {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public ReturnBookUseCase(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = Objects.requireNonNull(loanRepository, "LoanRepository cannot be null.");
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null.");
    }

    public Loan execute(ReturnBookCommand command) {
        Loan loan = loanRepository.findById(command.loanId())
                .orElseThrow(() -> new LoanNotFoundException("Loan not found: " + command.loanId()));

        loan.returnBook(command.returnedAt());

        Book book = bookRepository.findById(loan.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + loan.bookId()));
        book.returnCopy();

        loanRepository.save(loan);
        bookRepository.save(book);

        return loan;
    }
}
