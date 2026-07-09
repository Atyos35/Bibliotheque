package com.example.bibliotheque.domain.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.event.BookBorrowedEvent;
import com.example.bibliotheque.domain.event.BookReturnedEvent;
import com.example.bibliotheque.domain.event.DomainEvent;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoanTest {

    private static final BookId BOOK_ID = BookId.generate();
    private static final MemberId MEMBER_ID = MemberId.generate();
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 7, 9, 10, 0);

    @Test
    void borrowingComputesDueDateAsTwentyOneDaysAfterBorrowedAt() {
        Loan loan = Loan.borrow(LoanId.generate(), BOOK_ID, MEMBER_ID, NOW, List.of());

        assertThat(loan.borrowedAt()).isEqualTo(NOW);
        assertThat(loan.dueDate()).isEqualTo(NOW.plusDays(21));
        assertThat(loan.isActive()).isTrue();
    }

    @Test
    void borrowingEmitsBookBorrowedEvent() {
        LoanId loanId = LoanId.generate();

        Loan loan = Loan.borrow(loanId, BOOK_ID, MEMBER_ID, NOW, List.of());

        List<DomainEvent> events = loan.pullDomainEvents();
        assertThat(events).containsExactly(
                new BookBorrowedEvent(loanId, BOOK_ID, MEMBER_ID, NOW, NOW.plusDays(21)));
    }

    @Test
    void pullDomainEventsClearsEventsAfterRetrieval() {
        Loan loan = Loan.borrow(LoanId.generate(), BOOK_ID, MEMBER_ID, NOW, List.of());

        loan.pullDomainEvents();

        assertThat(loan.pullDomainEvents()).isEmpty();
    }

    @Test
    void memberWithTwoActiveLoansCanBorrowAThirdOne() {
        Loan firstLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW, List.of());
        Loan secondLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan));

        Loan thirdLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan, secondLoan));

        assertThat(thirdLoan).isNotNull();
    }

    @Test
    void memberWithThreeActiveLoansCannotBorrowAFourthOne() {
        Loan firstLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW, List.of());
        Loan secondLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan));
        Loan thirdLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan, secondLoan));

        assertThatThrownBy(() -> Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan, secondLoan, thirdLoan)))
                .isInstanceOf(LoanLimitExceededException.class);
    }

    @Test
    void returnedLoansDoNotCountTowardsTheActiveLoanLimit() {
        Loan firstLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW, List.of());
        Loan secondLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan));
        Loan thirdLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan, secondLoan));
        firstLoan.returnBook(NOW.plusDays(1));

        Loan fourthLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(firstLoan, secondLoan, thirdLoan));

        assertThat(fourthLoan).isNotNull();
    }

    @Test
    void memberWithAnOverdueActiveLoanCannotBorrowANewBook() {
        LocalDateTime overdueLoanBorrowedAt = NOW.minusDays(30);
        Loan overdueLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID,
                overdueLoanBorrowedAt, List.of());

        assertThatThrownBy(() -> Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(overdueLoan)))
                .isInstanceOf(OverdueLoanException.class);
    }

    @Test
    void loanExactlyAtItsDueDateIsNotYetConsideredOverdue() {
        LocalDateTime borrowedAtExactlyTwentyOneDaysAgo = NOW.minusDays(21);
        Loan loanDueToday = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID,
                borrowedAtExactlyTwentyOneDaysAgo, List.of());

        Loan newLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(loanDueToday));

        assertThat(newLoan).isNotNull();
    }

    @Test
    void memberWithAReturnedOverdueLoanCanBorrowANewBook() {
        LocalDateTime overdueLoanBorrowedAt = NOW.minusDays(30);
        Loan returnedOverdueLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID,
                overdueLoanBorrowedAt, List.of());
        returnedOverdueLoan.returnBook(NOW.minusDays(1));

        Loan newLoan = Loan.borrow(LoanId.generate(), BookId.generate(), MEMBER_ID, NOW,
                List.of(returnedOverdueLoan));

        assertThat(newLoan).isNotNull();
    }

    @Test
    void returningABookMarksReturnedAtAndEmitsBookReturnedEvent() {
        LoanId loanId = LoanId.generate();
        Loan loan = Loan.borrow(loanId, BOOK_ID, MEMBER_ID, NOW, List.of());
        loan.pullDomainEvents();
        LocalDateTime returnedAt = NOW.plusDays(5);

        loan.returnBook(returnedAt);

        assertThat(loan.isActive()).isFalse();
        assertThat(loan.returnedAt()).isEqualTo(returnedAt);
        assertThat(loan.pullDomainEvents()).containsExactly(
                new BookReturnedEvent(loanId, BOOK_ID, MEMBER_ID, returnedAt));
    }

    @Test
    void returningAnAlreadyReturnedLoanThrowsException() {
        Loan loan = Loan.borrow(LoanId.generate(), BOOK_ID, MEMBER_ID, NOW, List.of());
        loan.returnBook(NOW.plusDays(1));

        assertThatThrownBy(() -> loan.returnBook(NOW.plusDays(2)))
                .isInstanceOf(LoanAlreadyReturnedException.class);
    }
}
