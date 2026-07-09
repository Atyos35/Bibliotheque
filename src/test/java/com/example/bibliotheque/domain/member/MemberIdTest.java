package com.example.bibliotheque.domain.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MemberIdTest {

    @Test
    void generatesUniqueMemberIds() {
        MemberId first = MemberId.generate();
        MemberId second = MemberId.generate();

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void createsMemberIdFromValidUuidString() {
        UUID uuid = UUID.randomUUID();

        MemberId memberId = MemberId.of(uuid.toString());

        assertThat(memberId.value()).isEqualTo(uuid);
    }

    @Test
    void twoMemberIdWithTheSameValueAreEqual() {
        UUID uuid = UUID.randomUUID();

        assertThat(MemberId.of(uuid.toString())).isEqualTo(MemberId.of(uuid.toString()));
    }

    @Test
    void rejectsNullMemberId() {
        assertThatThrownBy(() -> MemberId.of(null))
                .isInstanceOf(InvalidMemberIdException.class);
    }

    @Test
    void rejectsBlankMemberId() {
        assertThatThrownBy(() -> MemberId.of("   "))
                .isInstanceOf(InvalidMemberIdException.class);
    }

    @Test
    void rejectsNonUuidMemberId() {
        assertThatThrownBy(() -> MemberId.of("not-a-uuid"))
                .isInstanceOf(InvalidMemberIdException.class);
    }
}
