# Multi-stage build for smaller image size
FROM maven:3.8.6-openjdk-11-slim AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy



# Install wget for healthcheck
RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Create non-root user
RUN groupadd -r hrms && useradd -r -g hrms hrms

# Copy JAR from build stage
COPY --from=build /app/target/hrms-0.0.1-SNAPSHOT.jar app.jar

# Change ownership
RUN chown -R hrms:hrms /app

# Switch to non-root user
USER hrms

# Expose port
EXPOSE 8080

# Health check (using wget or remove if not needed)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Environment variables (can be overridden)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# Run application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

