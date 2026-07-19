package com.example.bibliotheque.infrastructure.member;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository Spring Data JPA technique pour {@link MemberEntity}, utilisé en interne par
 * {@link JpaMemberRepository}.
 */
public interface SpringDataMemberRepository extends JpaRepository<MemberEntity, UUID> {
}
