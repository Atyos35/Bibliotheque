package com.example.bibliotheque.application.loan;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookRepository;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberCannotBorrowException;
import com.example.bibliotheque.domain.member.MemberRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public final class BorrowBookUseCase {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public BorrowBookUseCase(BookRepository bookRepository, MemberRepository memberRepository,
                              LoanRepository loanRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "BookRepository cannot be null.");
        this.memberRepository = Objects.requireNonNull(memberRepository, "MemberRepository cannot be null.");
        this.loanRepository = Objects.requireNonNull(loanRepository, "LoanRepository cannot be null.");
    }

    public Loan execute(BorrowBookCommand command) {
        Member member = memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberNotFoundException("Member not found: " + command.memberId()));
        if (!member.canBorrow()) {
            throw new MemberCannotBorrowException("Member " + command.memberId() + " cannot borrow a book.");
        }

        Book book = bookRepository.findById(command.bookId())
                .orElseThrow(() -> new BookNotFoundException("Book not found: " + command.bookId()));

        List<Loan> memberLoans = loanRepository.findByMemberId(command.memberId());
        Loan loan = Loan.borrow(LoanId.generate(), command.bookId(), command.memberId(), command.borrowedAt(),
                memberLoans);

        book.borrowCopy();

        loanRepository.save(loan);
        bookRepository.save(book);

        return loan;
    }
}
