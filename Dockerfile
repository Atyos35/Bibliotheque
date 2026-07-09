# Stage build : compile le jar avec Maven + JDK complet
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
# Tests et analyse statique tournent en CI (mvn clean verify), pas dans l'image.
RUN mvn -B clean package -DskipTests

# Stage runtime : JRE seul, aucun outil de build expedie en production
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/bibliotheque-*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
