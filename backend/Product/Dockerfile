FROM openjdk:21-jdk-slim

WORKDIR /app

# Copy the JAR file
COPY target/product-0.0.1-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8086

# Set environment variables
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka/
ENV SPRING_PROFILES_ACTIVE=docker

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]