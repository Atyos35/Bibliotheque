package com.example.bibliotheque.application.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.BookRepository;
import com.example.bibliotheque.domain.book.ISBN;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanAlreadyReturnedException;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReturnBookUseCaseTest {

    private static final LocalDateTime BORROWED_AT = LocalDateTime.of(2026, 7, 9, 10, 0);
    private static final LocalDateTime RETURNED_AT = BORROWED_AT.plusDays(5);

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    private ReturnBookUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReturnBookUseCase(loanRepository, bookRepository);
    }

    private static Book bookWithOneCopyBorrowed(BookId bookId) {
        return new Book(bookId, new ISBN("9780134685991"), "Effective Java", "Joshua Bloch", 2, 1);
    }

    private static Loan activeLoan(LoanId loanId, BookId bookId) {
        return Loan.borrow(loanId, bookId, MemberId.generate(), BORROWED_AT, List.of());
    }

    @Test
    void marksLoanReturnedAndIncrementsAvailableCopies() {
        BookId bookId = BookId.generate();
        LoanId loanId = LoanId.generate();
        Loan loan = activeLoan(loanId, bookId);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookWithOneCopyBorrowed(bookId)));

        Loan returnedLoan = useCase.execute(new ReturnBookCommand(loanId, RETURNED_AT));

        assertThat(returnedLoan.isActive()).isFalse();
        assertThat(returnedLoan.returnedAt()).isEqualTo(RETURNED_AT);
        verify(loanRepository).save(loan);
        verify(bookRepository).save(argThat(book -> book.availableCopies() == 2));
    }

    @Test
    void throwsLoanNotFoundExceptionWhenLoanDoesNotExist() {
        LoanId loanId = LoanId.generate();
        when(loanRepository.findById(loanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ReturnBookCommand(loanId, RETURNED_AT)))
                .isInstanceOf(LoanNotFoundException.class);

        verify(bookRepository, never()).save(any());
    }

    @Test
    void throwsBookNotFoundExceptionWhenLoanReferencesAnUnknownBook() {
        BookId bookId = BookId.generate();
        LoanId loanId = LoanId.generate();
        Loan loan = activeLoan(loanId, bookId);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new ReturnBookCommand(loanId, RETURNED_AT)))
                .isInstanceOf(BookNotFoundException.class);
    }

    @Test
    void throwsLoanAlreadyReturnedExceptionWhenLoanWasAlreadyReturned() {
        BookId bookId = BookId.generate();
        LoanId loanId = LoanId.generate();
        Loan loan = activeLoan(loanId, bookId);
        loan.returnBook(RETURNED_AT);
        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> useCase.execute(new ReturnBookCommand(loanId, RETURNED_AT.plusDays(1))))
                .isInstanceOf(LoanAlreadyReturnedException.class);

        verify(bookRepository, never()).save(any());
    }
}
