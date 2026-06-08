## Multi-module backend image: builds each module with Maven then runs all jars
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy entire project (backend directory is context root via railway.json)
COPY . /workspace/

# Build order: Utility first (with install), then other modules. Skip tests for faster builds.
RUN set -ex \
    && if [ -d /workspace/Utility ]; then cd /workspace/Utility && mvn -DskipTests install && cd /workspace; fi \
    && for d in Eureka Gate User Post Comment Like Friend Product Order Cart; do \
         if [ -d "/workspace/$d" ]; then \
           echo "Building module: $d"; \
           cd /workspace/$d && mvn -DskipTests package && cd /workspace; \
         fi; \
       done

FROM eclipse-temurin:21-jre
WORKDIR /app

# Create app directory
RUN mkdir -p /app

# Copy entire build workspace from build stage
COPY --from=build /workspace /workspace

# Find and copy all JAR files from build workspace
RUN find /workspace -name "*.jar" -type f -exec cp {} /app/ \; 2>/dev/null || true

# Copy startup script from build stage
COPY --from=build /workspace/backend-start.sh /app/backend-start.sh
RUN chmod +x /app/backend-start.sh

# Verify JARs were copied
RUN echo "JAR files in /app:" && ls -la /app/*.jar 2>/dev/null || echo "Checking for JARs..." && find /app -name "*.jar" -type f

EXPOSE 8761 8888 8081 8082 8083 8084 8085 8086 8087 8088

CMD ["/app/backend-start.sh"]
