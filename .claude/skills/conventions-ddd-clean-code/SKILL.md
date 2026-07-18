---
name: conventions-ddd-clean-code
description: Règles DDD tactiques et Clean Code à appliquer sur ce projet, backend Java/Spring et frontend Angular/TypeScript. Utiliser cette skill pour toute tâche de planification ou d'implémentation touchant au domaine métier (agrégats, value objects, use cases, repositories), au typage TypeScript, ou à la qualité générale du code (nommage, taille des fonctions, duplication, gestion des erreurs). Se déclenche sur les tickets de type "feature" ou "bug".
---

# Conventions DDD & Clean Code

## Règles DDD tactiques

- **Agrégats** : chaque agrégat protège ses propres invariants. Une méthode publique sur l'agrégat ne doit jamais laisser l'objet dans un état invalide, même temporairement. Pas de setters publics qui contournent une règle métier.
- **Value Objects** : immuables. Deux Value Objects avec les mêmes valeurs sont égaux (`equals`/`hashCode` sur la valeur, pas l'identité). Toute validation de format (ISBN, email...) vit dans le constructeur du Value Object, pas ailleurs.
- **Repositories** : l'interface vit dans `domain/`, l'implémentation dans `infrastructure/`. Le domaine ne connaît jamais Spring Data JPA, Hibernate, ou toute autre dépendance d'infrastructure.
- **Use Cases (Application)** : orchestrent le domaine, ne contiennent aucune règle métier elles-mêmes. Si une classe d'Application contient un `if` qui décide d'un comportement métier plutôt que d'appeler l'agrégat, c'est un signal que la règle est mal placée.
- **Interface (contrôleurs)** : traduisent la requête HTTP en commande applicative et la réponse applicative en HTTP. Aucune logique métier, aucun accès direct à un repository.
- **Domain Events** : un événement de domaine décrit un fait passé (`BookBorrowedEvent`, pas `BorrowBookCommand`). Il est immuable et ne contient que les données nécessaires à ses consommateurs.

## Critères Clean Code

- **Nommage explicite** : le nom d'une fonction ou d'une classe dit ce qu'elle fait sans qu'on ait besoin de lire son implémentation. Pas d'abréviations obscures.
- **Fonctions courtes, responsabilité unique** : si une méthode fait plus d'une chose, elle doit être découpée.
- **Pas de duplication (DRY)** : avant d'écrire une nouvelle méthode, vérifier qu'une méthode équivalente n'existe pas déjà dans l'agrégat ou le use case concerné.
- **Gestion des erreurs par exceptions** : une règle métier violée lève une exception de domaine explicite (`MemberSuspendedException`, `LoanLimitExceededException`), jamais un code de retour ou un booléen silencieux.
- **Commentaires** : expliquent le *pourquoi* (ex : pourquoi 21 jours, pourquoi 3 emprunts) plutôt que le *quoi*, qui doit être lisible dans le code lui-même.

## Avant d'écrire du code

1. Identifier dans quelle couche (Domain / Application / Infrastructure / Interface) chaque changement se situe.
2. Vérifier `docs/domaine.md` pour les invariants déjà établis avant d'en introduire un nouveau ou d'en modifier un existant.
3. Ne pas ajouter d'abstraction, de configuration, ou de flexibilité non demandée par le ticket — un ticket qui ajoute une règle ne doit pas devenir l'occasion de refactorer une couche entière.

## Typage TypeScript (Angular)

- `strict: true` dans `tsconfig.json` (déjà activé au scaffold, section 4.1) — ne jamais l'affaiblir pour contourner une erreur de type.
- Jamais de `any` explicite. Si le type est réellement inconnu à la compilation, utiliser `unknown` et le restreindre (narrowing) avant usage — pas de cast silencieux.
- Chaque réponse HTTP consommée par un service Angular a une interface dédiée qui reflète exactement le contrat du backend (`BookResponse`, `LoanResponse`...), jamais un objet anonyme ou un `Record<string, any>`.
- Les erreurs métier renvoyées par le backend (409 avec message, voir skill de l'étape Interface côté Java) sont typées elles aussi (`interface ApiError { message: string }`) plutôt que castées en `any` dans un `catchError`.
- Pas de `!` (non-null assertion) pour contourner le strict null checking — traiter explicitement le cas `null`/`undefined` plutôt que l'écarter par assertion.

## Design et UI (Angular)

- **Tailwind CSS** pour tout le style — jamais de CSS ad hoc dispersé par composant, jamais de contrôle HTML natif laissé sans classes (un `<button>` ou `<input>` non stylé ne sort jamais tel quel).
- **Palette du projet** : `emerald` comme couleur de marque (actions primaires, lien de navigation actif, badges de statut positif), `slate` comme neutre (texte, bordures, fonds), `red` réservé aux erreurs — ne pas introduire d'autre teinte sans raison sémantique claire. Les classes partagées vivent une seule fois dans `src/styles.css` (`@layer components`), jamais redéfinies en dur dans un composant.
- **Coquille d'application** : toute page vit dans `.app-shell` (fond `slate-50`, pleine hauteur) surmontée d'un `.app-header` (nom de l'app + navigation). Une page de contenu brut sans en-tête ni fond distinctif se lit comme un document, pas comme une application.
- **Surfaces** : le contenu principal d'une page (formulaire, panneau) vit dans une carte blanche (`.surface` ou `.card`) posée sur le fond de la coquille, jamais directement dessus — c'est ce contraste qui donne l'impression d'une interface plutôt que du texte brut sur fond blanc.
- **Statuts visuels** : un statut qui se lit d'un coup d'œil (disponibilité, état d'un emprunt...) passe par un badge (pastille arrondie, fond clair + texte de la même teinte), pas par du texte brut au milieu d'une phrase.
- **Cohérence inter-composants** : une palette et une typographie définies une fois (config Tailwind ou classes de base réutilisées), pas choisies au cas par cas à chaque nouveau composant. Un bouton d'action principal doit toujours se reconnaître visuellement d'un composant à l'autre.
- **États systématiquement stylés** : chargement (spinner ou skeleton, jamais juste "Loading..." en texte brut), erreur (message clair dans un bloc visuellement distinct, pas une alert() ni du texte rouge isolé), liste vide (message explicite plutôt qu'un écran blanc).
- **Espacement et hiérarchie visuelle** : marges/paddings cohérents (échelle Tailwind standard), titres et labels clairement différenciés du contenu.
- **Priorité desktop** : le contexte d'usage principal est une démo sur écran d'ordinateur (entretien, vidéo), pas un usage mobile — inutile de sur-investir dans un responsive mobile complet pour ce projet.

## Exemple

**Mauvais** (contrôle non stylé) :
```html
<button (click)="borrow()">Emprunter</button>
```

**Bon** (cohérent avec le design system du projet) :
```html
<button
  (click)="borrow()"
  class="rounded-lg bg-emerald-700 px-4 py-2 text-white font-medium hover:bg-emerald-800 transition-colors"
>
  Emprunter
</button>
```

**Mauvais** (règle métier dans le contrôleur) :
```java
@PostMapping("/loans")
public ResponseEntity<?> borrow(@RequestBody BorrowRequest req) {
    if (memberRepository.findById(req.memberId()).getActiveLoans().size() >= 3) {
        return ResponseEntity.badRequest().build();
    }
    // ...
}
```

**Bon** (règle métier dans l'agrégat, contrôleur mince) :
```java
@PostMapping("/loans")
public ResponseEntity<LoanResponse> borrow(@RequestBody BorrowRequest req) {
    Loan loan = borrowBookUseCase.execute(new BorrowBookCommand(req.memberId(), req.bookId()));
    return ResponseEntity.ok(LoanResponse.from(loan));
}
```

**Mauvais** (typage relâché côté Angular) :
```typescript
borrowBook(payload: any): Observable<any> {
  return this.http.post(this.apiUrl, payload);
}
```

**Bon** (contrat typé de bout en bout) :
```typescript
borrowBook(payload: BorrowRequest): Observable<LoanResponse> {
  return this.http.post<LoanResponse>(this.apiUrl, payload);
}
```
