# Plan de tournage — vidéo de démo (2-3 minutes)

Vidéo destinée à un recruteur : montrer le traitement d'un ticket de bout en bout, depuis la carte Kanban jusqu'à la fonctionnalité visible dans l'application, en passant par la Pull Request générée par l'agent. Durée totale visée : 2:30 à 2:45.

## Plans

| # | Plan | Durée | Écran / action | Script de narration |
|---|---|---|---|---|
| 1 | Ticket sur le repo pipeline | 20 s | Carte Kanban passée en colonne « Doing », puis Issue GitHub correspondante sur le repo `HumanInTheLoop`, avec ses sections Contexte / Critères d'acceptation / Hors périmètre. | « Ce ticket décrit une règle métier précise, avec des critères d'acceptation vérifiables et un périmètre explicitement borné — c'est ce texte qui sert de prompt à l'agent. » |
| 2 | Deux repos distincts | 15 s | Bascule entre l'onglet du repo `HumanInTheLoop` (pipeline) et l'onglet du repo `Bibliotheque` (code applicatif). | « Le pipeline qui exécute l'agent vit dans un repo séparé du code applicatif — un repo générique qui pourrait piloter n'importe quel autre projet, découplé de la logique métier qu'il ne fait que modifier depuis l'extérieur. » |
| 3 | PR générée sur le repo cible | 25 s | Pull Request ouverte sur `Bibliotheque`, avec sa description (résumé, ticket lié, checklist definition-of-done) et son statut. | « La Pull Request est ouverte automatiquement sur le repo cible, jamais mergée par l'agent — la description reprend le ticket et confirme que les tests et le lint sont passants avant même la revue humaine. » |
| 4 | Diff de code | 30 s | Onglet « Files changed » de la PR, code du use case et du contrôleur. | « Le diff montre que le changement respecte la séparation en couches du domaine : la règle ajoutée reste dans le domaine et l'application, sans logique métier dans le contrôleur REST. » |
| 5 | Tests qui passent | 20 s | Check CI vert sur la PR, ou sortie terminal de `mvn test` incluant le nouveau test écrit par l'agent. | « La suite de tests s'exécute et passe, y compris le test d'intégration ajouté pour ce ticket — c'est la condition que l'agent s'impose avant de pousser sa branche. » |
| 6 | Merge | 15 s | Clic sur le bouton de merge de la PR, effectué à l'écran par la personne qui présente. | « Le merge reste une action humaine : je relis la PR et je merge moi-même — l'agent n'a à aucun moment accès à ce bouton. » |
| 7 | Fonctionnalité qui tourne dans l'app | 40 s | Application relancée après merge, navigation jusqu'à la fonctionnalité concernée par le ticket (ex. formulaire d'emprunt), utilisation réelle dans l'interface. | « Une fois mergée et redéployée, la fonctionnalité est visible et utilisable dans l'application — le cycle complet va donc bien du ticket jusqu'au comportement observable, pas seulement jusqu'à une PR qui reste ouverte. » |

**Durée totale : 2 min 45 s.**

## Notes de tournage

- Garder chaque plan sur un seul écran à la fois (pas de superposition de fenêtres) pour rester lisible en export basse résolution (partage d'écran en entretien).
- Le plan 2 peut être raccourci à 10 s si le temps presse : l'essentiel est de nommer les deux repos et pourquoi ils sont séparés, pas de s'y attarder.
- Prévoir une prise complète en une fois si possible ; sinon, découper aux limites de plans ci-dessus pour faciliter le montage.
