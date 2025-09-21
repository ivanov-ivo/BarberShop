# Multi-stage build for better optimization
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apk add --no-cache \
    curl \
    bash

# Copy Maven wrapper and pom.xml first for better layer caching
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn
COPY pom.xml .
COPY checkstyle.xml .
COPY spotbugs-exclude.xml .
COPY sqlScript.sql .

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application with tests
RUN ./mvnw clean package

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache \
    curl

# Copy the built JAR from builder stage
COPY --from=builder /app/target/BarberShop-0.0.1-SNAPSHOT.jar app.jar

# Create directory for uploaded files
RUN mkdir -p /app/uploads

# Create non-root user for security
RUN addgroup -g 1001 barbershop && adduser -D -u 1001 -G barbershop barbershop
RUN chown -R barbershop:barbershop /app
USER barbershop

# Expose port 8080
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=docker
ENV SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/barbershop
ENV SPRING_DATASOURCE_USERNAME=barbershop
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
