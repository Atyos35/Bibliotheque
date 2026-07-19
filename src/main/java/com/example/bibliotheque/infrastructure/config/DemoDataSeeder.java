package com.example.bibliotheque.infrastructure.config;

import com.example.bibliotheque.domain.book.Book;
import com.example.bibliotheque.domain.book.BookId;
import com.example.bibliotheque.domain.book.BookRepository;
import com.example.bibliotheque.domain.book.ISBN;
import com.example.bibliotheque.domain.member.Email;
import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MemberRepository;
import com.example.bibliotheque.domain.member.MembershipStatus;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Jeu de données minimal pour qu'un lancement local ordinaire ({@code mvn spring-boot:run}, sans
 * profil supplémentaire) n'affiche pas un catalogue et un formulaire d'emprunt vides — aucun
 * endpoint de création de livre ou de membre n'existe pour les peupler autrement. IDs fixes
 * (documentés dans le README) plutôt que générés : sans endpoint {@code GET /members}, un ID
 * aléatoire ne serait pas découvrable pour tester le formulaire d'emprunt. Distinct
 * d'{@link E2eDataSeeder} (données dédiées aux scénarios Playwright) : exclu à la fois du profil
 * {@code test} (jamais pendant {@code mvn test}, voir les {@code @ActiveProfiles} des tests
 * {@code @SpringBootTest}) et du profil {@code e2e-seed}, pour ne jamais interférer avec l'un ou
 * l'autre. Jamais actif en profil {@code prod}.
 */
@Component
@Profile("dev & !test & !e2e-seed")
public class DemoDataSeeder implements CommandLineRunner {

    public static final String MEMBER_ID = "d0000000-0000-0000-0000-000000000001";
    public static final String BOOK_1984_ID = "d0000000-0000-0000-0000-000000000002";
    public static final String BOOK_DUNE_ID = "d0000000-0000-0000-0000-000000000003";

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public DemoDataSeeder(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) {
        bookRepository.save(Book.create(BookId.of(BOOK_1984_ID), new ISBN("9780451524935"), "1984",
                "George Orwell", 3));
        bookRepository.save(Book.create(BookId.of(BOOK_DUNE_ID), new ISBN("9780441013593"), "Dune",
                "Frank Herbert", 2));

        memberRepository.save(new Member(MemberId.of(MEMBER_ID), "Marie Curie",
                new Email("marie.curie@example.com"), MembershipStatus.ACTIVE));
    }
}
