package com.example.bibliotheque.application.loan;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookRepository;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanRepository;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Use case qui orchestre le retour d'un livre emprunté, aucune règle métier ici : la validation du
 * retour est déléguée à {@link Loan#returnBook(java.time.LocalDateTime)}.
 */
@Service
public final class ReturnBookUseCase {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;

    public ReturnBookUseCase(LoanRepository loanRepository, BookRepository bookRepository) {
        this.loanRepository = Objects.requireNonNull(loanRepository, "LoanRepository cannot be null.");
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null.");
    }

    /**
     * Retrouve l'emprunt, enregistre son retour (qui valide ses invariants), incrémente les
     * exemplaires disponibles du livre correspondant, puis persiste l'emprunt et le livre.
     */
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
