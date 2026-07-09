package com.example.bibliotheque.domain.member;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MemberTest {

    private static final Email VALID_EMAIL = new Email("jane.doe@example.com");

    @Test
    void activeMemberCanBorrow() {
        Member member = new Member(MemberId.generate(), "Jane Doe", VALID_EMAIL, MembershipStatus.ACTIVE);

        assertThat(member.canBorrow()).isTrue();
    }

    @Test
    void suspendedMemberCannotBorrow() {
        Member member = new Member(MemberId.generate(), "Jane Doe", VALID_EMAIL, MembershipStatus.SUSPENDED);

        assertThat(member.canBorrow()).isFalse();
    }
}
