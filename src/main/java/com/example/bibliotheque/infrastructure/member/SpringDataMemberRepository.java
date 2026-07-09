package com.example.bibliotheque.infrastructure.member;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMemberRepository extends JpaRepository<MemberEntity, UUID> {
}
