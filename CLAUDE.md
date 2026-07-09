# Contexte du projet

Application de démonstration en DDD (Domain-Driven Design), backend Java/Spring Boot + front Angular optionnel. Objectif : portfolio montrant à un recruteur le développement d'une fonctionnalité de bout en bout, piloté par ticket (GitHub Issue ou carte Trello), avec un agent Claude qui plane, code, teste et ouvre une PR — jamais ne merge.

Domaine détaillé dans `docs/domaine.md` — le lire avant toute tâche touchant au métier (`Book`, `Member`, `Loan`).

## Architecture (DDD tactique)

```
src/main/java/.../
├── domain/            Entités, Value Objects, Agrégats, interfaces de Repository, Domain Events
├── application/        Use Cases / Command Handlers — orchestrent le domaine, aucune règle métier ici
├── infrastructure/      Implémentations Spring Data JPA des repositories, adapters externes
└── interfaces/          Contrôleurs REST (@RestController) — traduisent HTTP en commande applicative
```

Règle non négociable : **aucune règle métier en dehors de `domain/`**. Un contrôleur ou un repository qui contient une décision métier (une condition sur l'état d'un `Loan`, par exemple) est un bug d'architecture, pas un détail.

## Commandes

- Build + tests backend : `mvn clean verify`
- Tests unitaires seuls : `mvn test`
- Analyse statique : `mvn checkstyle:check` puis `mvn spotbugs:check`
- Lancer l'app en local (profil dev, H2) : `mvn spring-boot:run`
- Docker : `docker compose up --build`
- Front Angular (si présent) : `npm install`, `ng serve`, `ng test`, `npx eslint .`

## Skills à respecter

- `conventions-ddd-clean-code` — pour toute tâche de plan ou d'implémentation touchant au domaine ou à la qualité générale du code.
- `devops-conventions` — pour toute tâche Docker, CI/CD, ou monitoring.
- `definition-of-done` — checklist à valider avant de committer.

Ces skills se chargent automatiquement quand la tâche correspond ; ne pas dupliquer leur contenu ici.

## Garde-fous

- Jamais de merge automatique. Le résultat attendu d'une tâche de développement est une branche poussée et une PR ouverte, pas un merge sur `main`.
- Jamais d'ajout hors du périmètre demandé par le ticket. Si une dépendance ou un fichier hors plan devient nécessaire, le signaler explicitement plutôt que l'ajouter silencieusement.
- Un ticket sans critères d'acceptation vérifiables ou sans section "Hors périmètre" renseignée n'est pas prêt — le signaler plutôt que deviner l'intention.
