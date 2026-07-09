package com.example.bibliotheque.interfaces;

import com.example.bibliotheque.application.loan.BookNotFoundException;
import com.example.bibliotheque.application.loan.LoanNotFoundException;
import com.example.bibliotheque.application.loan.MemberNotFoundException;
import com.example.bibliotheque.domain.book.BookNotAvailableException;
import com.example.bibliotheque.domain.book.InvalidBookCopiesException;
import com.example.bibliotheque.domain.book.InvalidBookIdException;
import com.example.bibliotheque.domain.book.InvalidIsbnException;
import com.example.bibliotheque.domain.loan.InvalidLoanIdException;
import com.example.bibliotheque.domain.loan.LoanAlreadyReturnedException;
import com.example.bibliotheque.domain.loan.LoanLimitExceededException;
import com.example.bibliotheque.domain.loan.OverdueLoanException;
import com.example.bibliotheque.domain.member.InvalidEmailException;
import com.example.bibliotheque.domain.member.InvalidMemberIdException;
import com.example.bibliotheque.domain.member.MemberCannotBorrowException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler({
        LoanLimitExceededException.class,
        OverdueLoanException.class,
        MemberCannotBorrowException.class,
        BookNotAvailableException.class,
        LoanAlreadyReturnedException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({
        MemberNotFoundException.class,
        BookNotFoundException.class,
        LoanNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler({
        InvalidBookIdException.class,
        InvalidMemberIdException.class,
        InvalidLoanIdException.class,
        InvalidIsbnException.class,
        InvalidEmailException.class,
        InvalidBookCopiesException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidInput(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No resource found."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred."));
    }
}
