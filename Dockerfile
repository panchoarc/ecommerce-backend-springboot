# ==========================
# Stage 1: Build
# ==========================
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# ==========================
# Stage 2: Runtime (Distroless)
# ==========================
FROM gcr.io/distroless/java17:nonroot
WORKDIR /app

# Copiamos el jar completo (ejecutable, con layers)
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Arranque seguro y rápido
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:+UseSerialGC","-XX:MaxRAMPercentage=75.0","-Xss512k","-jar","app.jar"]
