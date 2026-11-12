# ===================================================================
# CampusDesk Backend - Multi-stage Dockerfile (Java 21 + Maven)
# ===================================================================

FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Cache Maven dependencies first
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build executable JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S spring \
    && adduser -S spring -G spring

COPY --from=builder /build/target/*.jar app.jar
RUN chown -R spring:spring /app

USER spring:spring

EXPOSE 8051

HEALTHCHECK --interval=20s --timeout=5s --start-period=45s --retries=5 \
  CMD curl -sf http://localhost:8051/v3/api-docs >/dev/null || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
