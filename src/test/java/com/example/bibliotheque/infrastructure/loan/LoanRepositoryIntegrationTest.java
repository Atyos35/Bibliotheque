package com.example.bibliotheque.infrastructure.loan;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class LoanRepositoryIntegrationTest {

    private static final LocalDateTime BORROWED_AT = LocalDateTime.of(2026, 7, 9, 10, 0, 0);

    @Autowired
    private LoanRepository loanRepository;

    @Test
    void persistsAndReloadsAnActiveLoan() {
        BookId bookId = BookId.generate();
        MemberId memberId = MemberId.generate();
        Loan loan = Loan.borrow(LoanId.generate(), bookId, memberId, BORROWED_AT, List.of());

        loanRepository.save(loan);
        Optional<Loan> reloaded = loanRepository.findById(loan.id());

        assertThat(reloaded).isPresent();
        Loan reloadedLoan = reloaded.get();
        assertThat(reloadedLoan.id()).isEqualTo(loan.id());
        assertThat(reloadedLoan.bookId()).isEqualTo(bookId);
        assertThat(reloadedLoan.memberId()).isEqualTo(memberId);
        assertThat(reloadedLoan.borrowedAt()).isEqualTo(BORROWED_AT);
        assertThat(reloadedLoan.dueDate()).isEqualTo(BORROWED_AT.plusDays(21));
        assertThat(reloadedLoan.isActive()).isTrue();
        assertThat(reloadedLoan.returnedAt()).isNull();
    }

    @Test
    void persistsAndReloadsAReturnedLoan() {
        Loan loan = Loan.borrow(LoanId.generate(), BookId.generate(), MemberId.generate(), BORROWED_AT, List.of());
        LocalDateTime returnedAt = BORROWED_AT.plusDays(5);
        loan.returnBook(returnedAt);

        loanRepository.save(loan);
        Optional<Loan> reloaded = loanRepository.findById(loan.id());

        assertThat(reloaded).isPresent();
        assertThat(reloaded.get().isActive()).isFalse();
        assertThat(reloaded.get().returnedAt()).isEqualTo(returnedAt);
    }

    @Test
    void findsLoansByMemberId() {
        MemberId memberId = MemberId.generate();
        Loan loan = Loan.borrow(LoanId.generate(), BookId.generate(), memberId, BORROWED_AT, List.of());
        loanRepository.save(loan);

        List<Loan> memberLoans = loanRepository.findByMemberId(memberId);

        assertThat(memberLoans).extracting(Loan::id).containsExactly(loan.id());
    }
}
