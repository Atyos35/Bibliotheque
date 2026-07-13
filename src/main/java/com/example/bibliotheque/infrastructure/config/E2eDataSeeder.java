package com.example.bibliotheque.infrastructure.config;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.BookRepository;
import com.example.bibliotheque.domain.book.ISBN;
import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.Email;
import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MemberRepository;
import com.example.bibliotheque.domain.member.MembershipStatus;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Jeu de données fixe pour les tests E2E Playwright, qui pilotent l'application réelle par HTTP
 * et n'ont donc aucun autre moyen d'atteindre un état donné (aucun endpoint de création de livre
 * ou de membre n'existe). Actif uniquement sous le profil {@code e2e-seed}, jamais activé par
 * {@code mvn test} (profil {@code dev} seul) ni en profil {@code prod}.
 */
@Component
@Profile("e2e-seed")
public class E2eDataSeeder implements CommandLineRunner {

    /** Jamais emprunté par aucun scénario E2E — sert uniquement à vérifier l'affichage du catalogue. */
    public static final String CATALOG_BOOK_ID = "e2e00000-0000-0000-0000-000000000001";
    public static final String CATALOG_BOOK_TITLE = "Clean Code";
    public static final String CATALOG_BOOK_AUTHOR = "Robert C. Martin";
    public static final int CATALOG_BOOK_TOTAL_COPIES = 2;

    /** Emprunté par le scénario "emprunt réussi" — totalCopies volontairement élevé pour rester
     * disponible même après plusieurs exécutions locales successives sans redémarrer le backend. */
    public static final String BORROWABLE_BOOK_ID = "e2e00000-0000-0000-0000-000000000002";

    public static final String ACTIVE_MEMBER_ID = "e2e00000-0000-0000-0000-000000000101";
    public static final String SUSPENDED_MEMBER_ID = "e2e00000-0000-0000-0000-000000000102";
    public static final String MEMBER_AT_LOAN_LIMIT_ID = "e2e00000-0000-0000-0000-000000000103";

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public E2eDataSeeder(BookRepository bookRepository, MemberRepository memberRepository,
                          LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public void run(String... args) {
        bookRepository.save(Book.create(BookId.of(CATALOG_BOOK_ID), new ISBN("9780132350884"), CATALOG_BOOK_TITLE,
                CATALOG_BOOK_AUTHOR, CATALOG_BOOK_TOTAL_COPIES));
        bookRepository.save(Book.create(BookId.of(BORROWABLE_BOOK_ID), new ISBN("9780134685991"), "Effective Java",
                "Joshua Bloch", 100));

        memberRepository.save(new Member(MemberId.of(ACTIVE_MEMBER_ID), "Ada Lovelace",
                new Email("ada.lovelace@example.test"), MembershipStatus.ACTIVE));
        memberRepository.save(new Member(MemberId.of(SUSPENDED_MEMBER_ID), "Grace Hopper",
                new Email("grace.hopper@example.test"), MembershipStatus.SUSPENDED));
        memberRepository.save(new Member(MemberId.of(MEMBER_AT_LOAN_LIMIT_ID), "Alan Turing",
                new Email("alan.turing@example.test"), MembershipStatus.ACTIVE));

        MemberId memberAtLimit = MemberId.of(MEMBER_AT_LOAN_LIMIT_ID);
        for (int i = 0; i < Loan.MAX_ACTIVE_LOANS_PER_MEMBER; i++) {
            LocalDateTime borrowedAt = LocalDateTime.now();
            loanRepository.save(Loan.reconstitute(LoanId.generate(), BookId.generate(), memberAtLimit, borrowedAt,
                    borrowedAt.plusDays(Loan.LOAN_DURATION_DAYS), null));
        }
    }
}
