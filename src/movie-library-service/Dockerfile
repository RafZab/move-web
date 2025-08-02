# ========================================
# STAGE 1: Build Stage
# ========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /opt/app

COPY pom.xml ./
COPY src ./src

RUN mvn clean install -DskipTests

# ========================================
# STAGE 2: Runtime Stage
# ========================================
FROM eclipse-temurin:21.0.7_6-jre-alpine-3.21

COPY --from=builder /opt/app/target/*.jar spring-boot-app.jar

CMD ["java", "-jar", "spring-boot-app.jar"]
