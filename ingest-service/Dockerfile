# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy minimal POMs first to leverage Docker layer cache
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY ingest-service/pom.xml ./ingest-service/pom.xml
# Optional: include other module POMs to resolve -am (keeps cache valid if they change)
COPY orders-service/pom.xml ./orders-service/pom.xml
COPY risk-service/pom.xml ./risk-service/pom.xml
COPY settlements-service/pom.xml ./settlements-service/pom.xml

# Pre-fetch dependencies
RUN mvn -q -B -DskipTests dependency:go-offline

# Now copy the full source
COPY . .

# Build only the ingest-service module (and required deps)
RUN mvn -q -B -DskipTests -pl ingest-service -am package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy
ENV APP_HOME=/app
WORKDIR ${APP_HOME}

# Non-root for security (Render supports)
RUN useradd -u 10001 appuser
USER 10001

# Copy the built jar
COPY --from=build /app/ingest-service/target/ingest-service-*.jar ${APP_HOME}/app.jar

# Render sets PORT; our application.yml reads it
EXPOSE 8080

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]