---
name: definition-of-done
description: Checklist à valider avant de committer ou d'ouvrir une pull request sur ce projet. Utiliser systématiquement en fin de tâche, avant de proposer un commit ou une PR.
---

# Definition of Done

Avant de committer ou d'ouvrir une PR, confirmer chaque point :

- [ ] Les tests unitaires et d'intégration liés au changement passent (`mvn test` / `ng test`).
- [ ] L'analyse statique ne remonte aucune erreur (`mvn checkstyle:check`, `mvn spotbugs:check`, `npx eslint .` selon la couche touchée).
- [ ] Aucun secret, token, ou identifiant n'a été ajouté en clair dans le code, la config, ou l'historique de commit.
- [ ] Aucun fichier modifié en dehors du périmètre décrit par le plan ou le ticket — toute exception est signalée explicitement, pas silencieuse.
- [ ] Le message de commit suit Conventional Commits (`feat:`, `fix:`, `test:`, `chore(devops):`, `docs:`) et décrit le changement, pas l'implémentation interne.
- [ ] La description de la PR résume le changement, référence le ticket, et confirme explicitement que tests et lint sont passants.
- [ ] Aucune règle métier n'a été introduite en dehors de `domain/` (voir skill `conventions-ddd-clean-code`).
- [ ] Le changement n'a pas été mergé — une PR ouverte est le résultat final attendu, pas un merge sur `main`.

Si un de ces points n'est pas rempli, le signaler explicitement plutôt que de proposer le commit ou la PR tel quel.
