package com.example.bibliotheque.application.loan;

import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MemberRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

/**
 * Use case qui orchestre la consultation des emprunts actifs d'un membre, aucune règle métier ici :
 * le filtrage s'appuie sur {@link Loan#isActive()}.
 */
@Service
public final class ListActiveLoansUseCase {

    private final MemberRepository memberRepository;
    private final LoanRepository loanRepository;

    public ListActiveLoansUseCase(MemberRepository memberRepository, LoanRepository loanRepository) {
        this.memberRepository = Objects.requireNonNull(memberRepository, "MemberRepository cannot be null.");
        this.loanRepository = Objects.requireNonNull(loanRepository, "LoanRepository cannot be null.");
    }

    /**
     * Vérifie que le membre existe, puis retourne ses emprunts en cours (non encore rendus).
     * Lève {@link MemberNotFoundException} si le membre est inconnu.
     */
    public List<Loan> execute(MemberId memberId) {
        Objects.requireNonNull(memberId, "MemberId cannot be null.");
        memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("Member not found: " + memberId));

        return loanRepository.findByMemberId(memberId).stream()
                .filter(Loan::isActive)
                .toList();
    }
}
