# Multi-stage Dockerfile for Library Management System
# Build stage
FROM maven:3.9.6-eclipse-temurin-22-alpine AS build

WORKDIR /app

# Cache dependencies for faster builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -Pprod

# Runtime stage
FROM eclipse-temurin:22-jdk-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/libraryapi.jar app.jar

# Set ownership and switch to non-root user
RUN chown -R spring:spring /app
USER spring

# JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]