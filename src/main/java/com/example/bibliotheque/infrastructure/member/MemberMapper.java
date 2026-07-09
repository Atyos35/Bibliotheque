package com.example.bibliotheque.infrastructure.member;

import com.example.bibliotheque.domain.member.Email;
import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MembershipStatus;

public final class MemberMapper {

    private MemberMapper() {
    }

    public static MemberEntity toEntity(Member member) {
        return new MemberEntity(member.id().value(), member.name(), member.email().value(),
                member.membershipStatus().name());
    }

    public static Member toDomain(MemberEntity entity) {
        return new Member(MemberId.of(entity.getId().toString()), entity.getName(), new Email(entity.getEmail()),
                MembershipStatus.valueOf(entity.getMembershipStatus()));
    }
}
