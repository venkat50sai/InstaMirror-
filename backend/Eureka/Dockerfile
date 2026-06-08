# Use Java 21 JDK
FROM eclipse-temurin:21-jdk-alpine

# Set Author
LABEL authors="ARUN KUMAR"

# Set working directory
WORKDIR /app

# Copy your Spring Boot JAR into the container
COPY target/eureka-0.0.1-SNAPSHOT.jar app.jar

# Expose Eureka port
EXPOSE 8761

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]