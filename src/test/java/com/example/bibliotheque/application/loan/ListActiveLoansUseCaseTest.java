package com.example.bibliotheque.application.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.Email;
import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MemberRepository;
import com.example.bibliotheque.domain.member.MembershipStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListActiveLoansUseCaseTest {

    private static final LocalDateTime BORROWED_AT = LocalDateTime.of(2026, 7, 9, 10, 0);

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LoanRepository loanRepository;

    private ListActiveLoansUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListActiveLoansUseCase(memberRepository, loanRepository);
    }

    private static Member activeMember(MemberId memberId) {
        return new Member(memberId, "Jane Doe", new Email("jane.doe@example.com"), MembershipStatus.ACTIVE);
    }

    private static Loan activeLoan(MemberId memberId) {
        return Loan.borrow(LoanId.generate(), BookId.generate(), memberId, BORROWED_AT, List.of());
    }

    @Test
    void returnsOnlyActiveLoansOfTheMember() {
        MemberId memberId = MemberId.generate();
        Loan activeLoan = activeLoan(memberId);
        Loan returnedLoan = activeLoan(memberId);
        returnedLoan.returnBook(BORROWED_AT.plusDays(5));
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(activeMember(memberId)));
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of(activeLoan, returnedLoan));

        List<Loan> result = useCase.execute(memberId);

        assertThat(result).containsExactly(activeLoan);
    }

    @Test
    void returnsEmptyListWhenMemberHasNoActiveLoan() {
        MemberId memberId = MemberId.generate();
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(activeMember(memberId)));
        when(loanRepository.findByMemberId(memberId)).thenReturn(List.of());

        List<Loan> result = useCase.execute(memberId);

        assertThat(result).isEmpty();
    }

    @Test
    void throwsMemberNotFoundExceptionWhenMemberDoesNotExist() {
        MemberId memberId = MemberId.generate();
        when(memberRepository.findById(memberId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(memberId))
                .isInstanceOf(MemberNotFoundException.class);
    }
}
