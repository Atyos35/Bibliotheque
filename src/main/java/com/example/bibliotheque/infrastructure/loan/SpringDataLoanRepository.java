package com.example.bibliotheque.infrastructure.loan;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataLoanRepository extends JpaRepository<LoanEntity, UUID> {

    List<LoanEntity> findByMemberId(UUID memberId);
}
