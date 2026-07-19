package com.example.bibliotheque.domain.member;

import java.util.Optional;

/**
 * Port du domaine pour la persistance des {@link Member}. L'implémentation concrète vit dans la couche
 * infrastructure ; le domaine ne connaît que cette interface.
 */
public interface MemberRepository {

    Optional<Member> findById(MemberId id);

    void save(Member member);
}
