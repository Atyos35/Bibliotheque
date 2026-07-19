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

/**
 * Traduit les exceptions levées par le domaine et la couche applicative en réponses HTTP.
 * Centralise ce mapping ici évite que les contrôleurs n'aient à connaître les détails des
 * exceptions métier : chaque groupe ci-dessous correspond à une catégorie d'échec (violation
 * de règle métier, ressource introuvable, entrée invalide) traduite vers un code HTTP unique.
 */
@RestControllerAdvice
public class DomainExceptionHandler {

    /**
     * Règles métier violées lors d'une opération par ailleurs valide (limite d'emprunts
     * atteinte, emprunt en retard, membre ne pouvant plus emprunter, livre indisponible,
     * emprunt déjà retourné) -> 409 Conflict : la requête est correcte mais l'état actuel
     * du domaine empêche de l'honorer.
     */
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

    /** Entité métier introuvable (membre, livre ou emprunt) -> 404 Not Found. */
    @ExceptionHandler({
        MemberNotFoundException.class,
        BookNotFoundException.class,
        LoanNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(exception.getMessage()));
    }

    /**
     * Identifiants ou valeurs mal formés (id, ISBN, email, nombre d'exemplaires) -> 400 Bad
     * Request : la requête elle-même est invalide, indépendamment de l'état du domaine.
     */
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

    /** Aucune route/ressource statique ne correspond à l'URL demandée -> 404 Not Found. */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(NoResourceFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("No resource found."));
    }

    /**
     * Filet de sécurité pour toute exception non prévue par les gestionnaires précédents
     * -> 500 Internal Server Error, sans exposer le détail technique au client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred."));
    }
}
