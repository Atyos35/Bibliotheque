package com.example.bibliotheque.domain.loan;

import com.example.bibliotheque.domain.member.MemberId;
import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Optional<Loan> findById(LoanId id);

    List<Loan> findByMemberId(MemberId memberId);

    void save(Loan loan);
}
