---
name: devops-conventions
description: Conventions Docker, CI/CD (GitHub Actions) et monitoring pour ce projet. Utiliser pour toute tâche de planification ou d'implémentation touchant à la conteneurisation, aux pipelines CI/CD, ou à l'observabilité. Se déclenche sur les tickets de type "devops".
---

# Conventions DevOps

## Docker

- Dockerfile multi-stage systématique : un stage `build` (image JDK complète + Maven) et un stage `runtime` (image JRE légère, ex. `eclipse-temurin:21-jre-alpine`), pour ne jamais expédier les outils de build en production.
- Jamais de secret en dur dans un Dockerfile ou une image. Toujours via variable d'environnement injectée au run.
- Un `HEALTHCHECK` explicite dans le Dockerfile ou le `docker-compose.yml` pour tout service qui expose une API.

## CI/CD (GitHub Actions)

- Un job dédié par responsabilité (build, test, lint, analyse statique) plutôt qu'un seul job monolithique — plus lisible dans l'historique des runs et plus facile à faire échouer précisément.
- Les secrets (tokens, clés API) passent toujours par `secrets.*` ou `vars.*`, jamais en clair dans le YAML.
- Un job qui touche à une infra partagée (déploiement, migration de base) ne s'exécute jamais automatiquement sur `main` sans étape d'approbation explicite (`environment` avec required reviewers, ou déclenchement manuel `workflow_dispatch`).

## Monitoring / observabilité

- Nommage des métriques : `<domaine>_<action>_<unite>` (ex. `loan_borrowed_total`, `loan_overdue_gauge`), cohérent avec les conventions Prometheus (snake_case, suffixe d'unité explicite).
- Un healthcheck applicatif (`/actuator/health` côté Spring Boot) distinct d'un healthcheck infra (le conteneur répond) — les deux ne mesurent pas la même chose.
- Toute alerte doit avoir un seuil justifié dans un commentaire ou la description de la PR — pas de seuil arbitraire non documenté.

## Garde-fou renforcé (tickets DevOps)

Un ticket DevOps touche presque toujours une zone sensible. Par défaut :
- Jamais de déploiement automatique en production — PR uniquement, revue humaine obligatoire avant merge.
- Ne jamais modifier une stratégie de scaling, une politique de rétention de données, ou un accès réseau existant sans que le ticket le demande explicitement.
