# Bibliotheque

Application Java/Spring Boot avec module frontend Angular (à venir).

## Lancer l'application

La configuration est portée par `src/main/resources/application.yml`, avec deux profils Spring : `dev` et `prod`. Aucune valeur sensible n'est écrite en dur dans le dépôt — le profil `prod` lit sa configuration exclusivement depuis des variables d'environnement.

### Profil `dev` — H2 en mémoire (aucune dépendance externe)

`dev` est le profil actif par défaut : aucune base de données à installer, la base H2 est créée en mémoire au démarrage et détruite à l'arrêt.

```bash
mvn spring-boot:run
```

La console H2 est disponible sur `http://localhost:8080/h2-console` (JDBC URL : `jdbc:h2:mem:bibliotheque`, utilisateur `sa`, mot de passe vide).

### Profil `prod` — PostgreSQL

Le profil `prod` nécessite une instance PostgreSQL accessible et les variables d'environnement suivantes (aucune valeur par défaut n'est fournie) :

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

Ou, à partir du jar packagé :

```bash
java -jar target/bibliotheque-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

En profil `prod`, le schéma n'est jamais généré automatiquement (`ddl-auto: validate`) — la base doit déjà exister avec le schéma attendu.
