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

/**
 * Agrégat racine représentant l'emprunt d'un livre par un membre. Protège les invariants métier de
 * l'emprunt (limite d'emprunts actifs, blocage en cas de retard) et collecte les {@link DomainEvent}
 * émis lors des transitions (emprunt, retour) pour publication ultérieure.
 */
public final class Loan {

    /** Durée d'un emprunt en jours : {@code dueDate = borrowedAt + LOAN_DURATION_DAYS}. */
    public static final int LOAN_DURATION_DAYS = 21;
    /** Nombre maximal d'emprunts actifs simultanés autorisé pour un même membre. */
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

    /**
     * Crée un nouvel emprunt pour un membre, en validant les invariants métier avant toute création.
     * {@code memberLoans} doit contenir l'historique des emprunts (actifs ou non) du membre, nécessaire
     * pour évaluer ces règles :
     * <ul>
     *     <li>si le membre a un emprunt actif déjà en retard à la date {@code borrowedAt}, l'emprunt est
     *     refusé et {@link OverdueLoanException} est levée — un membre en retard doit d'abord régulariser
     *     sa situation avant d'emprunter à nouveau ;</li>
     *     <li>si le membre a déjà {@link #MAX_ACTIVE_LOANS_PER_MEMBER} emprunts actifs, l'emprunt est
     *     refusé et {@link LoanLimitExceededException} est levée.</li>
     * </ul>
     * La date d'échéance ({@code dueDate}) est calculée automatiquement à {@code borrowedAt +
     * LOAN_DURATION_DAYS} jours. Un {@link BookBorrowedEvent} est émis en cas de succès.
     */
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

    /**
     * Reconstruit un emprunt existant à partir de données déjà validées (typiquement depuis la
     * persistance, ou en tests). Contrairement à {@link #borrow}, aucune règle métier n'est revérifiée
     * ici : ni la limite d'emprunts actifs, ni le blocage en cas de retard. Cette méthode ne doit donc
     * jamais être utilisée pour créer un nouvel emprunt à l'initiative d'un membre.
     */
    public static Loan reconstitute(LoanId id, BookId bookId, MemberId memberId, LocalDateTime borrowedAt,
                                     LocalDateTime dueDate, LocalDateTime returnedAt) {
        Loan loan = new Loan(id, bookId, memberId, borrowedAt, dueDate);
        loan.returnedAt = returnedAt;
        return loan;
    }

    /**
     * Marque l'emprunt comme retourné à la date donnée. Lève {@link LoanAlreadyReturnedException} si
     * l'emprunt a déjà été retourné. Émet un {@link BookReturnedEvent} en cas de succès.
     */
    public void returnBook(LocalDateTime returnedAt) {
        Objects.requireNonNull(returnedAt, "returnedAt cannot be null.");
        if (!isActive()) {
            throw new LoanAlreadyReturnedException("Loan " + id + " has already been returned.");
        }
        this.returnedAt = returnedAt;
        domainEvents.add(new BookReturnedEvent(id, bookId, memberId, returnedAt));
    }

    /** Indique si l'emprunt est en cours, c'est-à-dire non encore retourné. */
    public boolean isActive() {
        return returnedAt == null;
    }

    /** Indique si l'emprunt est actif et que sa date d'échéance est dépassée à l'instant {@code at}. */
    public boolean isOverdue(LocalDateTime at) {
        return isActive() && dueDate.isBefore(at);
    }

    /**
     * Retourne les événements de domaine accumulés depuis la dernière lecture, puis vide le tampon
     * interne : chaque événement n'est ainsi renvoyé qu'une seule fois, à charge pour l'appelant de les publier.
     */
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
