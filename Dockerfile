# ---- Build Stage ----
# Using Amazon Corretto 22 for reliable, production-grade Java builds
FROM amazoncorretto:22.0.1-alpine AS build

# Install Maven in the build stage
RUN apk add --no-cache maven

WORKDIR /app

# Cache dependencies for faster builds
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Build application
COPY src ./src
RUN mvn clean package -DskipTests -Pprod

# ---- Runtime Stage ----
# Using Amazon Corretto 22 for production runtime
FROM amazoncorretto:22.0.1-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/libraryapi.jar app.jar

# Set ownership and switch to non-root user
RUN chown -R spring:spring /app
USER spring

# JVM options optimized for container environments
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]