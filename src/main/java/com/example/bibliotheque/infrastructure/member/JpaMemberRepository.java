package com.example.bibliotheque.infrastructure.member;

import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MemberRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Implémentation JPA du port {@link MemberRepository} défini par le domaine. Adaptateur technique qui
 * délègue la persistance effective à {@link SpringDataMemberRepository} et traduit via {@link MemberMapper}.
 */
@Repository
public class JpaMemberRepository implements MemberRepository {

    private final SpringDataMemberRepository springDataMemberRepository;

    public JpaMemberRepository(SpringDataMemberRepository springDataMemberRepository) {
        this.springDataMemberRepository = springDataMemberRepository;
    }

    @Override
    public Optional<Member> findById(MemberId id) {
        return springDataMemberRepository.findById(id.value()).map(MemberMapper::toDomain);
    }

    @Override
    public void save(Member member) {
        springDataMemberRepository.save(MemberMapper.toEntity(member));
    }
}
