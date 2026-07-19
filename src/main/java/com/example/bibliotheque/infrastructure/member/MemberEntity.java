package com.example.bibliotheque.infrastructure.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Entité JPA de persistance du membre. Simple structure de données pour Hibernate : ne porte aucune
 * règle métier, celle-ci reste dans l'agrégat {@link com.example.bibliotheque.domain.member.Member}.
 */
@Entity
@Table(name = "members")
public class MemberEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String membershipStatus;

    protected MemberEntity() {
    }

    public MemberEntity(UUID id, String name, String email, String membershipStatus) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.membershipStatus = membershipStatus;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }
}
