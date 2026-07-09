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
import com.example.bibliotheque.domain.book.BookNotAvailableException;
import com.example.bibliotheque.domain.book.BookRepository;
import com.example.bibliotheque.domain.book.ISBN;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanLimitExceededException;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.loan.OverdueLoanException;
import com.example.bibliotheque.domain.member.Email;
import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberCannotBorrowException;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MembershipStatus;
import com.example.bibliotheque.domain.member.MemberRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BorrowBookUseCaseTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 7, 9, 10, 0);

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    private BorrowBookUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new BorrowBookUseCase(bookRepository, memberRepository, loanRepository);
    }

    private static Member activeMember(MemberId memberId) {
        return new Member(memberId, "Jane Doe", new Email("jane.doe@example.com"), MembershipStatus.ACTIVE);
    }

    private static Book availableBook(BookId bookId, int totalCopies) {
        return Book.create(bookId, new ISBN("9780134685991"), "Effective Java", "Joshua Bloch", totalCopies);
    }

    @Test
    void createsLoanAndDecrementsAvailableCopiesWhenMemberActiveAndBookAvailable() {
        MemberId memberId = MemberId.generate();
        BookId bookId = BookId.generate();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(activeMember(memberId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(availableBook(bookId, 2)));
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of());

        Loan loan = useCase.execute(new BorrowBookCommand(memberId, bookId, NOW));

        assertThat(loan.memberId()).isEqualTo(memberId);
        assertThat(loan.bookId()).isEqualTo(bookId);
        assertThat(loan.dueDate()).isEqualTo(NOW.plusDays(21));

        verify(loanRepository).save(loan);
        verify(bookRepository).save(argThat(book -> book.availableCopies() == 1));
    }

    @Test
    void throwsMemberNotFoundExceptionWhenMemberDoesNotExist() {
        MemberId memberId = MemberId.generate();
        BookId bookId = BookId.generate();
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new BorrowBookCommand(memberId, bookId, NOW)))
                .isInstanceOf(MemberNotFoundException.class);

        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void throwsMemberCannotBorrowExceptionWhenMemberIsSuspendedWithoutTouchingBookOrLoan() {
        MemberId memberId = MemberId.generate();
        BookId bookId = BookId.generate();
        Member suspendedMember =
                new Member(memberId, "Jane Doe", new Email("jane.doe@example.com"), MembershipStatus.SUSPENDED);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(suspendedMember));

        assertThatThrownBy(() -> useCase.execute(new BorrowBookCommand(memberId, bookId, NOW)))
                .isInstanceOf(MemberCannotBorrowException.class);

        verify(bookRepository, never()).findById(any());
        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void throwsBookNotFoundExceptionWhenBookDoesNotExist() {
        MemberId memberId = MemberId.generate();
        BookId bookId = BookId.generate();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(activeMember(memberId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new BorrowBookCommand(memberId, bookId, NOW)))
                .isInstanceOf(BookNotFoundException.class);

        verify(loanRepository, never()).save(any());
    }

    @Test
    void throwsLoanLimitExceededExceptionWhenMemberAlreadyHasThreeActiveLoansWithoutTouchingBook() {
        MemberId memberId = MemberId.generate();
        BookId bookId = BookId.generate();
        Loan firstLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, NOW, List.of());
        Loan secondLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, NOW, List.of(firstLoan));
        Loan thirdLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, NOW,
                List.of(firstLoan, secondLoan));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(activeMember(memberId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(availableBook(bookId, 1)));
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of(firstLoan, secondLoan, thirdLoan));

        assertThatThrownBy(() -> useCase.execute(new BorrowBookCommand(memberId, bookId, NOW)))
                .isInstanceOf(LoanLimitExceededException.class);

        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void throwsOverdueLoanExceptionWhenMemberHasAnOverdueActiveLoan() {
        MemberId memberId = MemberId.generate();
        BookId bookId = BookId.generate();
        Loan overdueLoan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, NOW.minusDays(30),
                List.of());
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(activeMember(memberId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(availableBook(bookId, 1)));
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of(overdueLoan));

        assertThatThrownBy(() -> useCase.execute(new BorrowBookCommand(memberId, bookId, NOW)))
                .isInstanceOf(OverdueLoanException.class);

        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }

    @Test
    void throwsBookNotAvailableExceptionWhenBookHasNoAvailableCopyWithoutPersistingAnything() {
        MemberId memberId = MemberId.generate();
        BookId bookId = BookId.generate();
        Book bookWithoutAvailableCopy = new Book(bookId, new ISBN("9780134685991"), "Effective Java",
                "Joshua Bloch", 1, 0);
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(activeMember(memberId)));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookWithoutAvailableCopy));
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of());

        assertThatThrownBy(() -> useCase.execute(new BorrowBookCommand(memberId, bookId, NOW)))
                .isInstanceOf(BookNotAvailableException.class);

        verify(bookRepository, never()).save(any());
        verify(loanRepository, never()).save(any());
    }
}
