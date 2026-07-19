package com.example.bibliotheque.infrastructure.loan;

import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.member.MemberId;

/** Traduit un {@link Loan} du domaine vers/depuis sa représentation de persistance {@link LoanEntity}. */
public final class LoanMapper {

    private LoanMapper() {
    }

    public static LoanEntity toEntity(Loan loan) {
        return new LoanEntity(loan.id().value(), loan.bookId().value(), loan.memberId().value(),
                loan.borrowedAt(), loan.dueDate(), loan.returnedAt());
    }

    public static Loan toDomain(LoanEntity entity) {
        return Loan.reconstitute(
                LoanId.of(entity.getId().toString()),
                BookId.of(entity.getBookId().toString()),
                MemberId.of(entity.getMemberId().toString()),
                entity.getBorrowedAt(),
                entity.getDueDate(),
                entity.getReturnedAt());
    }
}
