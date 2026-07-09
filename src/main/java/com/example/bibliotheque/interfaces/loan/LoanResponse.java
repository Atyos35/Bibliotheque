package com.example.bibliotheque.interfaces.loan;

import com.example.bibliotheque.domain.loan.Loan;
import java.time.LocalDateTime;

public record LoanResponse(
        String id,
        String bookId,
        String memberId,
        LocalDateTime borrowedAt,
        LocalDateTime dueDate,
        LocalDateTime returnedAt,
        boolean active) {

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.id().toString(),
                loan.bookId().toString(),
                loan.memberId().toString(),
                loan.borrowedAt(),
                loan.dueDate(),
                loan.returnedAt(),
                loan.isActive());
    }
}
