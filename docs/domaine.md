# Domaine : gestion de prêts de bibliothèque

Référence métier pour toute tâche touchant au domaine. Chaque ticket doit rester borné à une seule règle de ce document.

## Agrégats

### `Book` (agrégat racine)
- Identité : `BookId` (Value Object, UUID).
- Attributs : `ISBN` (Value Object, validé au format ISBN-13), `title`, `author`, `totalCopies`, `availableCopies`.
- Invariant : `availableCopies` ne peut jamais dépasser `totalCopies`, ni être négatif.

### `Member` (agrégat racine)
- Identité : `MemberId` (Value Object, UUID).
- Attributs : `name`, `email` (Value Object, validé), `membershipStatus` (ACTIVE / SUSPENDED).
- Invariant : un membre `SUSPENDED` ne peut initier aucun nouvel emprunt.

### `Loan` (agrégat racine)
- Identité : `LoanId` (Value Object, UUID).
- Attributs : `bookId`, `memberId`, `borrowedAt`, `dueDate`, `returnedAt` (nullable).
- Invariants :
  - Un membre ne peut avoir plus de **3 emprunts actifs simultanés** (`returnedAt == null`).
  - Un membre avec au moins un emprunt en retard (`dueDate` dépassée et `returnedAt == null`) ne peut initier aucun nouvel emprunt tant que le retard n'est pas résolu.
  - `dueDate` est fixée à 21 jours après `borrowedAt` à la création.

## Domain Events

- `BookBorrowedEvent` — émis quand un emprunt est créé.
- `BookReturnedEvent` — émis au retour.
- `LoanOverdueEvent` — émis (par un batch ou à la lecture) quand `dueDate` est dépassée sans retour.

## Use cases déjà couverts (à ne pas redévelopper sans ticket dédié)

- `BorrowBookUseCase` : vérifie les invariants (`Member` actif, pas de retard en cours, moins de 3 emprunts actifs, `Book.availableCopies > 0`), crée le `Loan`, décrémente `availableCopies`.
- `ReturnBookUseCase` : marque `returnedAt`, incrémente `availableCopies`.

## Ce qui n'est volontairement pas couvert (hors périmètre du domaine actuel)

- Pas de réservation de livre déjà emprunté.
- Pas de calcul de pénalité financière de retard.
- Pas de gestion de plusieurs bibliothèques/succursales.

Toute extension de ces règles doit passer par un ticket dédié avec son propre champ "Hors périmètre" — ne pas étendre le domaine par déduction.
