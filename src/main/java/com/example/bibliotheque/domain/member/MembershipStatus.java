package com.example.bibliotheque.domain.member;

/**
 * Statut d'adhésion d'un {@link Member}. Seul le statut {@link #ACTIVE} autorise l'emprunt de livres,
 * voir {@link Member#canBorrow()} ; un membre {@link #SUSPENDED} ne peut initier aucun nouvel emprunt.
 */
public enum MembershipStatus {
    ACTIVE,
    SUSPENDED
}
