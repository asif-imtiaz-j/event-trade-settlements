# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy minimal POMs first for cache efficiency
COPY pom.xml ./pom.xml
COPY common/pom.xml ./common/pom.xml
COPY risk-service/pom.xml ./risk-service/pom.xml
# Include other module POMs to satisfy -am resolution
COPY ingest-service/pom.xml ./ingest-service/pom.xml
COPY orders-service/pom.xml ./orders-service/pom.xml
COPY settlements-service/pom.xml ./settlements-service/pom.xml

# Pre-fetch dependencies
RUN mvn -q -B -DskipTests dependency:go-offline

# Copy full source
COPY . .

# Build only risk-service (and required deps)
RUN mvn -q -B -DskipTests -pl risk-service -am package

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-jammy
ENV APP_HOME=/app
WORKDIR ${APP_HOME}

# Run as non-root
RUN useradd -u 10001 appuser
USER 10001

# Copy built jar
COPY --from=build /app/risk-service/target/risk-service-*.jar ${APP_HOME}/app.jar

# Default port is 8082; Render sets PORT env and app reads it from application.yml
EXPOSE 8082

ENTRYPOINT ["java","-XX:MaxRAMPercentage=75.0","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]