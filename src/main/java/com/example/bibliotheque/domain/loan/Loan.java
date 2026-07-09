package com.example.bibliotheque.domain.loan;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.event.BookBorrowedEvent;
import com.example.bibliotheque.domain.event.BookReturnedEvent;
import com.example.bibliotheque.domain.event.DomainEvent;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Loan {

    public static final int LOAN_DURATION_DAYS = 21;
    public static final int MAX_ACTIVE_LOANS_PER_MEMBER = 3;

    private final LoanId id;
    private final BookId bookId;
    private final MemberId memberId;
    private final LocalDateTime borrowedAt;
    private final LocalDateTime dueDate;
    private final List<DomainEvent> domainEvents = new ArrayList<>();
    private LocalDateTime returnedAt;

    private Loan(LoanId id, BookId bookId, MemberId memberId, LocalDateTime borrowedAt, LocalDateTime dueDate) {
        this.id = Objects.requireNonNull(id, "LoanId cannot be null.");
        this.bookId = Objects.requireNonNull(bookId, "BookId cannot be null.");
        this.memberId = Objects.requireNonNull(memberId, "MemberId cannot be null.");
        this.borrowedAt = Objects.requireNonNull(borrowedAt, "borrowedAt cannot be null.");
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate cannot be null.");
    }

    public static Loan borrow(LoanId id, BookId bookId, MemberId memberId, LocalDateTime borrowedAt,
                               List<Loan> memberLoans) {
        Objects.requireNonNull(memberLoans, "memberLoans cannot be null.");
        List<Loan> activeLoans = memberLoans.stream().filter(Loan::isActive).toList();

        if (activeLoans.stream().anyMatch(loan -> loan.isOverdue(borrowedAt))) {
            throw new OverdueLoanException(
                    "Member " + memberId + " has an overdue loan and cannot borrow a new book.");
        }
        if (activeLoans.size() >= MAX_ACTIVE_LOANS_PER_MEMBER) {
            throw new LoanLimitExceededException(
                    "Member " + memberId + " already has " + MAX_ACTIVE_LOANS_PER_MEMBER + " active loans.");
        }

        LocalDateTime dueDate = borrowedAt.plusDays(LOAN_DURATION_DAYS);
        Loan loan = new Loan(id, bookId, memberId, borrowedAt, dueDate);
        loan.domainEvents.add(new BookBorrowedEvent(id, bookId, memberId, borrowedAt, dueDate));
        return loan;
    }

    public static Loan reconstitute(LoanId id, BookId bookId, MemberId memberId, LocalDateTime borrowedAt,
                                     LocalDateTime dueDate, LocalDateTime returnedAt) {
        Loan loan = new Loan(id, bookId, memberId, borrowedAt, dueDate);
        loan.returnedAt = returnedAt;
        return loan;
    }

    public void returnBook(LocalDateTime returnedAt) {
        Objects.requireNonNull(returnedAt, "returnedAt cannot be null.");
        if (!isActive()) {
            throw new LoanAlreadyReturnedException("Loan " + id + " has already been returned.");
        }
        this.returnedAt = returnedAt;
        domainEvents.add(new BookReturnedEvent(id, bookId, memberId, returnedAt));
    }

    public boolean isActive() {
        return returnedAt == null;
    }

    public boolean isOverdue(LocalDateTime at) {
        return isActive() && dueDate.isBefore(at);
    }

    public List<DomainEvent> pullDomainEvents() {
        List<DomainEvent> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return events;
    }

    public LoanId id() {
        return id;
    }

    public BookId bookId() {
        return bookId;
    }

    public MemberId memberId() {
        return memberId;
    }

    public LocalDateTime borrowedAt() {
        return borrowedAt;
    }

    public LocalDateTime dueDate() {
        return dueDate;
    }

    public LocalDateTime returnedAt() {
        return returnedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Loan)) {
            return false;
        }
        Loan loan = (Loan) o;
        return id.equals(loan.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
