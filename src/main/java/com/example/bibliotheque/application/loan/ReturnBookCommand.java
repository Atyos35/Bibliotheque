package com.example.bibliotheque.application.loan;

import com.example.bibliotheque.domain.loan.LoanId;
import java.time.LocalDateTime;
import java.util.Objects;

public record ReturnBookCommand(LoanId loanId, LocalDateTime returnedAt) {

    public ReturnBookCommand {
        Objects.requireNonNull(loanId, "LoanId cannot be null.");
        Objects.requireNonNull(returnedAt, "returnedAt cannot be null.");
    }
}
