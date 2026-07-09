package com.example.bibliotheque.domain.member;

import java.util.Objects;

public final class Member {

    private final MemberId id;
    private final String name;
    private final Email email;
    private final MembershipStatus membershipStatus;

    public Member(MemberId id, String name, Email email, MembershipStatus membershipStatus) {
        this.id = Objects.requireNonNull(id, "MemberId cannot be null.");
        this.name = Objects.requireNonNull(name, "name cannot be null.");
        this.email = Objects.requireNonNull(email, "Email cannot be null.");
        this.membershipStatus = Objects.requireNonNull(membershipStatus, "membershipStatus cannot be null.");
    }

    public boolean canBorrow() {
        return membershipStatus == MembershipStatus.ACTIVE;
    }

    public MemberId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Email email() {
        return email;
    }

    public MembershipStatus membershipStatus() {
        return membershipStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member)) {
            return false;
        }
        Member member = (Member) o;
        return id.equals(member.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
