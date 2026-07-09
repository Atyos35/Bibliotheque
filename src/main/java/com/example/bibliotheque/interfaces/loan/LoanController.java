package com.example.bibliotheque.interfaces.loan;

import com.example.bibliotheque.application.loan.BorrowBookCommand;
import com.example.bibliotheque.application.loan.BorrowBookUseCase;
import com.example.bibliotheque.application.loan.ReturnBookCommand;
import com.example.bibliotheque.application.loan.ReturnBookUseCase;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.member.MemberId;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final BorrowBookUseCase borrowBookUseCase;
    private final ReturnBookUseCase returnBookUseCase;

    public LoanController(BorrowBookUseCase borrowBookUseCase, ReturnBookUseCase returnBookUseCase) {
        this.borrowBookUseCase = borrowBookUseCase;
        this.returnBookUseCase = returnBookUseCase;
    }

    @PostMapping
    public ResponseEntity<LoanResponse> borrow(@RequestBody BorrowBookRequest request) {
        BorrowBookCommand command = new BorrowBookCommand(
                MemberId.of(request.memberId()),
                BookId.of(request.bookId()),
                LocalDateTime.now());
        Loan loan = borrowBookUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(LoanResponse.from(loan));
    }

    @PostMapping("/{loanId}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable String loanId) {
        ReturnBookCommand command = new ReturnBookCommand(LoanId.of(loanId), LocalDateTime.now());
        Loan loan = returnBookUseCase.execute(command);
        return ResponseEntity.ok(LoanResponse.from(loan));
    }
}
