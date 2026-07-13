# Bibliotheque

Application de démonstration : le développement d'une fonctionnalité de bout en bout — ticket, plan, code, tests, PR — sur un backend Java/Spring Boot structuré en Domain-Driven Design, avec un frontend Angular optionnel et un agent Claude Code intervenant à chaque étape sauf le merge.

## Architecture

Le backend suit une architecture DDD tactique en quatre couches :

```
src/main/java/.../
├── domain/            Entités, Value Objects, Agrégats (Book, Member, Loan),
│                       interfaces de Repository, Domain Events
├── application/        Use Cases / Command Handlers — orchestrent le domaine,
│                       aucune règle métier ici
├── infrastructure/      Implémentations Spring Data JPA des repositories,
│                       adapters externes
└── interfaces/          Contrôleurs REST (@RestController) — traduisent
                        HTTP en commande applicative
```

Règle non négociable : aucune règle métier en dehors de `domain/`. Un contrôleur ou un repository qui contient une décision métier (une condition sur l'état d'un `Loan`, par exemple) est un bug d'architecture.

Le domaine (agrégats `Book`, `Member`, `Loan`, invariants, événements) est détaillé dans [docs/domaine.md](docs/domaine.md).

Le frontend Angular (`frontend/`) est structuré par feature (`books/`, `loans/`), pas par type de fichier.

## Lancer le projet

### Backend — profil `dev`, H2 en mémoire (aucune dépendance externe)

`dev` est le profil actif par défaut : aucune base de données à installer, H2 est créée en mémoire au démarrage et détruite à l'arrêt.

```bash
mvn spring-boot:run
```

Console H2 disponible sur `http://localhost:8080/h2-console` (JDBC URL : `jdbc:h2:mem:bibliotheque`, utilisateur `sa`, mot de passe vide).

### Backend — profil `prod`, PostgreSQL

Le profil `prod` lit sa configuration exclusivement depuis des variables d'environnement (aucune valeur sensible en dur dans le dépôt, `ddl-auto: validate` — le schéma doit déjà exister) :

| Variable | Description |
|---|---|
| `DB_HOST` | Hôte du serveur PostgreSQL |
| `DB_PORT` | Port du serveur PostgreSQL |
| `DB_NAME` | Nom de la base de données |
| `DB_USERNAME` | Utilisateur de connexion |
| `DB_PASSWORD` | Mot de passe de connexion |

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=bibliotheque
export DB_USERNAME=bibliotheque
export DB_PASSWORD=change-me
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Ou à partir du jar packagé :

```bash
java -jar target/bibliotheque-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Compose — application + PostgreSQL

`docker-compose.yml` démarre le service `postgres` (avec `HEALTHCHECK`) et construit l'application depuis le `Dockerfile` multi-stage (build Maven puis image `eclipse-temurin:21-jre-alpine`), en profil `prod`.

```bash
cp .env.example .env
# renseigner DB_HOST=postgres, DB_NAME, DB_USERNAME, DB_PASSWORD dans .env
docker compose up --build
```

L'application est exposée sur `http://localhost:8080`.

### Frontend Angular

```bash
cd frontend
npm install
ng serve
```

Le serveur de dev Angular tourne sur `http://localhost:4200` et proxie les appels `/api` vers le backend sur `http://localhost:8080` (`proxy.conf.json`) — le backend doit donc être lancé en parallèle (profil `dev` suffit).

## Tests et analyse statique

### Backend

```bash
mvn clean verify       # build + tests
mvn test                # tests unitaires et d'intégration seuls
mvn checkstyle:check    # style de code
mvn spotbugs:check      # détection de bugs potentiels
```

### Frontend

```bash
cd frontend
ng test                 # tests unitaires (Karma/Jasmine)
npx eslint .             # lint
ng build                 # vérifie la compilation en mode strict
```

### Tests E2E (Playwright)

Les tests E2E (`frontend/e2e/`) pilotent l'application réelle par HTTP, dans un vrai navigateur — pas de mock, contrairement aux tests de composant ci-dessus. Cela demande les **deux serveurs démarrés simultanément**, plus un jeu de données fixe côté backend (aucun endpoint n'existe pour créer un livre ou un membre via l'API, donc les scénarios ont besoin d'un état connu à l'avance).

**1. Backend, avec le profil `e2e-seed` en plus de `dev`** — insère au démarrage un livre jamais emprunté (pour le scénario catalogue), un livre dédié aux emprunts (pour le scénario succès), un membre actif, un membre `SUSPENDED`, et un membre ayant déjà 3 emprunts actifs. Ce profil n'est jamais actif par défaut, ni sous `mvn test`, ni en profil `prod` — voir `E2eDataSeeder`.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev,e2e-seed
```

**2. Frontend, dans un second terminal :**

```bash
cd frontend
ng serve
```

**3. Tests, dans un troisième terminal** (première utilisation : `npx playwright install chromium` pour télécharger le navigateur) :

```bash
cd frontend
npx playwright test
```

Deux scénarios sont couverts :
- `e2e/catalog.spec.ts` — la page `/books` affiche un livre du catalogue avec sa disponibilité (`X/Y exemplaires`).
- `e2e/borrow.spec.ts` — sur `/loans` : un emprunt réussi affiche le message de confirmation ; un membre suspendu ou un membre ayant déjà 3 emprunts actifs se voit refuser l'emprunt avec le message métier renvoyé par le backend (409).

Le scénario d'emprunt réussi rend le prêt via l'API juste après avoir vérifié la confirmation, pour rester rejouable plusieurs fois de suite sans redémarrer le backend.

## Pipeline de traitement des tickets

Les tickets de ce projet ne sont pas codés à la main : chaque évolution part d'une carte Trello déplacée en colonne "Doing", validée puis transformée en GitHub Issue, prise en charge par un agent Claude Code exécuté en mode headless qui plane, code, teste et ouvre une Pull Request sur ce dépôt — sans jamais merger automatiquement. La revue et le merge restent une action humaine.

Ce pipeline (gabarits de ticket, validation, orchestration GitHub Actions, invocation de l'agent) vit dans un dépôt séparé, générique, découplé de cette application cible :

- Dépôt pipeline : [github.com/Atyos35/HumanInTheLoop](https://github.com/Atyos35/HumanInTheLoop)
- Description complète du flux (Kanban → validation → agent → PR) : [workflow_agent_ia_kanban.md](https://github.com/Atyos35/HumanInTheLoop/blob/main/workflow_agent_ia_kanban.md)
