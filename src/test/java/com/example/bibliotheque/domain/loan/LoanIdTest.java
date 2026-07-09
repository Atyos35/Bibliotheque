package com.example.bibliotheque.domain.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class LoanIdTest {

    @Test
    void generatesUniqueLoanIds() {
        LoanId first = LoanId.generate();
        LoanId second = LoanId.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void createsLoanIdFromValidUuidString() {
        UUID uuid = UUID.randomUUID();

        LoanId loanId = LoanId.of(uuid.toString());

        assertThat(loanId.value()).isEqualTo(uuid);
    }

    @Test
    void rejectsNullLoanId() {
        assertThatThrownBy(() -> LoanId.of(null))
                .isInstanceOf(InvalidLoanIdException.class);
    }

    @Test
    void rejectsNonUuidLoanId() {
        assertThatThrownBy(() -> LoanId.of("not-a-uuid"))
                .isInstanceOf(InvalidLoanIdException.class);
    }
}
