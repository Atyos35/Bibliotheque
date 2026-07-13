package com.example.bibliotheque.domain.member;

import java.util.Optional;

public interface MemberRepository {

    Optional<Member> findById(MemberId id);

    void save(Member member);
}
