/**
 * Identifiants et données fixes insérés par le seeder backend
 * (src/main/java/com/example/bibliotheque/infrastructure/config/E2eDataSeeder.java),
 * actif uniquement quand le backend démarre avec le profil `e2e-seed` en plus de `dev`
 * (voir README, section Tests E2E). Toute modification des valeurs côté Java doit être
 * répercutée ici.
 */

export const CATALOG_BOOK = {
  title: 'Clean Code',
  author: 'Robert C. Martin',
  totalCopies: 2
};

export const BORROWABLE_BOOK_ID = 'e2e00000-0000-0000-0000-000000000002';

export const ACTIVE_MEMBER_ID = 'e2e00000-0000-0000-0000-000000000101';
export const SUSPENDED_MEMBER_ID = 'e2e00000-0000-0000-0000-000000000102';
export const MEMBER_AT_LOAN_LIMIT_ID = 'e2e00000-0000-0000-0000-000000000103';
