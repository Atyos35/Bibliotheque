package com.example.bibliotheque.infrastructure.member;

import com.example.bibliotheque.domain.member.Member;
import com.example.bibliotheque.domain.member.MemberId;
import com.example.bibliotheque.domain.member.MemberRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

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
}
