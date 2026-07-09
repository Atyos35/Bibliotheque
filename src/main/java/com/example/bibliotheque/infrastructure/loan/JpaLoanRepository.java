package com.example.bibliotheque.infrastructure.loan;

import com.example.bibliotheque.domain.loan.Loan;
import com.example.bibliotheque.domain.loan.LoanId;
import com.example.bibliotheque.domain.loan.LoanRepository;
import com.example.bibliotheque.domain.member.MemberId;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class JpaLoanRepository implements LoanRepository {

    private final SpringDataLoanRepository springDataLoanRepository;

    public JpaLoanRepository(SpringDataLoanRepository springDataLoanRepository) {
        this.springDataLoanRepository = springDataLoanRepository;
    }

    @Override
    public Optional<Loan> findById(LoanId id) {
        return springDataLoanRepository.findById(id.value()).map(LoanMapper::toDomain);
    }

    @Override
    public List<Loan> findByMemberId(MemberId memberId) {
        return springDataLoanRepository.findByMemberId(memberId.value()).stream()
                .map(LoanMapper::toDomain)
                .toList();
    }

    @Override
    public void save(Loan loan) {
        springDataLoanRepository.save(LoanMapper.toEntity(loan));
    }
}
