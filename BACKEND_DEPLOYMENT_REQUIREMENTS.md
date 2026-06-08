# Backend Deployment Requirements - Detailed

**Project:** Spring Boot Microservices Architecture  
**Java Version:** 21  
**Spring Boot Version:** 3.2.6  
**Spring Cloud Version:** 2023.0.6  
**Database:** MySQL 8.0+  
**Last Updated:** May 2026

---

## Table of Contents
1. [System Requirements](#system-requirements)
2. [All Microservices Overview](#all-microservices-overview)
3. [Service Registry (Eureka) - Detailed](#service-registry-eureka---detailed)
4. [API Gateway (Gate) - Detailed](#api-gateway-gate---detailed)
5. [Microservices - Individual Requirements](#microservices---individual-requirements)
6. [Shared Library (Utility) - Requirements](#shared-library-utility---requirements)
7. [Maven Build Configuration](#maven-build-configuration)
8. [Deployment Architecture](#deployment-architecture)
9. [Environment Variables - Complete Reference](#environment-variables---complete-reference)
10. [Database Setup - Complete Guide](#database-setup---complete-guide)
11. [Docker Containerization](#docker-containerization)
12. [Kubernetes Deployment](#kubernetes-deployment)
13. [Performance Tuning](#performance-tuning)
14. [Troubleshooting Guide](#troubleshooting-guide)

---

## System Requirements

### Minimum Hardware
```
CPU: 4 cores (can run on 2 cores with performance degradation)
RAM: 8GB total (2GB minimum per service cluster)
Storage: 50GB (OS + source + builds + database)
Network: 100Mbps (minimum)
Disk I/O: SSD recommended for database performance
```

### Operating System
- **Linux:** Ubuntu 20.04 LTS, CentOS 8+, or Debian 10+
- **Container:** Docker 20.10+
- **Kubernetes:** 1.20+ (if using K8s)
- **Process Management:** systemd, Supervisor, or PM2

### Java Runtime
```bash
# JDK 21 installation
# Option 1: OpenJDK (recommended)
sudo apt-get update
sudo apt-get install openjdk-21-jdk

# Option 2: Oracle JDK
# Download from oracle.com

# Verify installation
java -version
javac -version

# Set JAVA_HOME (add to ~/.bashrc or ~/.bash_profile)
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### Apache Maven
```bash
# Install Maven 3.8.1+
wget https://archive.apache.org/dist/maven/maven-3/3.8.6/binaries/apache-maven-3.8.6-bin.tar.gz
tar xzf apache-maven-3.8.6-bin.tar.gz
sudo mv apache-maven-3.8.6 /opt/maven

# Add to PATH
export M2_HOME=/opt/maven
export PATH=$M2_HOME/bin:$PATH

# Verify installation
mvn -version
```

---

## All Microservices Overview

### Service Architecture Table

| Service | Port | Purpose | DB | Dependencies |
|---------|------|---------|----|----|
| **Eureka** | 8761 | Service Registry & Discovery | None | Spring Cloud Eureka Server |
| **Gate** | 8888 | API Gateway & Routing | Optional | Eureka, Spring Cloud Gateway, JWT |
| **User** | 8081 | User Management | user_db | Eureka, JPA, Validation |
| **Post** | 8082 | Post Management | post_db | Eureka, JPA, Validation |
| **Comment** | 8083 | Comment Management | comment_db | Eureka, JPA, Validation |
| **Like** | 8084 | Like/Vote System | like_db | Eureka, JPA, Validation |
| **Friend** | 8085 | Friend Management | friend_db | Eureka, JPA, Validation |
| **Product** | 8086 | Product Catalog | product_db | Eureka, JPA, Validation |
| **Order** | 8087 | Order Management | order_db | Eureka, JPA, Validation |
| **Cart** | 8088 | Shopping Cart | cart_db | Eureka, JPA, Validation |

### Service Dependencies
```
┌─────────────────────────────────────────────────────────┐
│                    Frontend (Angular)                   │
│                   Port 80/443 (HTTPS)                   │
└────────────────────────┬────────────────────────────────┘
                         │
                    HTTP/REST
                         │
         ┌───────────────┴───────────────┐
         │                               │
         │                        HTTPS (TLS 1.2+)
         │                               │
    ┌────▼──────────────────────────────▼────┐
    │    API Gateway (Gate) - Port 8888       │
    │    ├─ Route /user/** -> User Service    │
    │    ├─ Route /post/** -> Post Service    │
    │    ├─ Route /comment/** -> Comment      │
    │    ├─ Route /like/** -> Like Service    │
    │    ├─ Route /friend/** -> Friend        │
    │    ├─ Route /product/** -> Product      │
    │    ├─ Route /order/** -> Order Service  │
    │    └─ Route /cart/** -> Cart Service    │
    └────┬──────────────────────────┬─────────┘
         │                          │
         │            Service Discovery
         │            (Eureka Registry)
         │                    │
    ┌────▼──────────────────▼──────────────┐
    │  Service Registry (Eureka)            │
    │  Port: 8761                           │
    │  ├─ User Service Registration         │
    │  ├─ Post Service Registration         │
    │  ├─ Comment Service Registration      │
    │  ├─ Like Service Registration         │
    │  ├─ Friend Service Registration       │
    │  ├─ Product Service Registration      │
    │  ├─ Order Service Registration        │
    │  └─ Cart Service Registration         │
    └────────────────────────┬──────────────┘
         ▲                    │
         │ Service Load       │ Service
         │ Balancing         │ Registry
         │                   │
    ┌────┴───────┬────────┬──┴─────┬────────┐
    │            │        │        │        │
┌───▼──┐  ┌──────▼──┐ ┌──▼────┐ ┌──▼────┐ ┌──▼────┐
│User  │  │Post     │ │Comment│ │Like    │ │Friend │
│8081  │  │8082     │ │8083   │ │8084    │ │8085   │
└──────┘  └─────────┘ └───────┘ └────────┘ └───────┘
     ▲         ▲        ▲         ▲          ▲
     │         │        │         │          │
    ┌┴─────────┴────────┴─────────┴──────────┴────┐
    │                                              │
    │        MySQL Database Cluster                │
    │                                              │
    │ ├─ user_db          (User Service)          │
    │ ├─ post_db          (Post Service)          │
    │ ├─ comment_db       (Comment Service)       │
    │ ├─ like_db          (Like Service)          │
    │ ├─ friend_db        (Friend Service)        │
    │ ├─ product_db       (Product Service)       │
    │ ├─ order_db         (Order Service)         │
    │ └─ cart_db          (Cart Service)          │
    │                                              │
    │ Master-Slave Replication (Optional)         │
    │ Read Replicas: user_db_read1, etc.          │
    └──────────────────────────────────────────────┘
```

---

## Service Registry (Eureka) - Detailed

### File Location
```
backend/Eureka/
├── pom.xml                          (Maven configuration)
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/app/eureka/      (Application code)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── target/
    ├── eureka-0.0.1-SNAPSHOT.jar   (Compiled JAR)
    └── ...
```

### Dependency Configuration (pom.xml)
```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2023.0.1</spring-cloud.version>
</properties>

<dependencies>
    <!-- Spring Cloud Eureka Server -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Application Configuration (application.properties)
```properties
# Server Port
server.port=8761
server.servlet.context-path=/

# Eureka Server Configuration
spring.application.name=eureka-server
eureka.server.enable-self-preservation=false
eureka.server.eviction-interval-timer-in-ms=10000
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=false

# Logging
logging.level.root=INFO
logging.level.com.netflix.eureka=DEBUG
logging.level.org.springframework.cloud=DEBUG
```

### Production Configuration
```properties
# server.port=8761
# eureka.instance.hostname=eureka.yourdomain.com
# eureka.server.enable-self-preservation=true
# eureka.server.eviction-interval-timer-in-ms=60000
```

### Docker Containerization
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/eureka-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Health Check
```bash
# Verify Eureka is running
curl http://localhost:8761/actuator/health
# Response: {"status":"UP"}

# View registered services
curl http://localhost:8761/eureka/apps
# Returns XML with all registered services
```

### Startup Command
```bash
# Standard startup
java -jar Eureka/target/eureka-0.0.1-SNAPSHOT.jar

# With custom hostname (for distributed deployment)
java -Deureka.instance.hostname=eureka.yourdomain.com \
  -Dserver.port=8761 \
  -jar Eureka/target/eureka-0.0.1-SNAPSHOT.jar

# With logging to file
java -jar Eureka/target/eureka-0.0.1-SNAPSHOT.jar \
  --logging.file.name=/var/log/eureka.log
```

---

## API Gateway (Gate) - Detailed

### File Location
```
backend/Gate/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/app/gate/       (Gateway code)
│   │   │       ├── config/         (Route configuration)
│   │   │       ├── filter/         (Request/Response filters)
│   │   │       └── security/       (JWT authentication)
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── target/
    ├── gate-0.0.1-SNAPSHOT.jar    (Compiled JAR)
    └── ...
```

### Dependency Configuration (pom.xml)
```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2023.0.1</spring-cloud.version>
</properties>

<dependencies>
    <!-- Spring Cloud Gateway -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>

    <!-- Eureka Client -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- Actuator (Health/Metrics) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>

    <!-- Utility Library -->
    <dependency>
        <groupId>com.app.utility</groupId>
        <artifactId>Utility</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

### Application Configuration (application.properties)
```properties
# Server
server.port=8888
server.servlet.context-path=/

# Application
spring.application.name=gate

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=false

# Spring Cloud Gateway Routes
spring.cloud.gateway.routes[0].id=user-service
spring.cloud.gateway.routes[0].uri=lb://user
spring.cloud.gateway.routes[0].predicates[0]=Path=/user/**

spring.cloud.gateway.routes[1].id=post-service
spring.cloud.gateway.routes[1].uri=lb://post
spring.cloud.gateway.routes[1].predicates[0]=Path=/post/**

spring.cloud.gateway.routes[2].id=comment-service
spring.cloud.gateway.routes[2].uri=lb://comment
spring.cloud.gateway.routes[2].predicates[0]=Path=/comment/**

spring.cloud.gateway.routes[3].id=like-service
spring.cloud.gateway.routes[3].uri=lb://like
spring.cloud.gateway.routes[3].predicates[0]=Path=/like/**

spring.cloud.gateway.routes[4].id=friend-service
spring.cloud.gateway.routes[4].uri=lb://friend
spring.cloud.gateway.routes[4].predicates[0]=Path=/friend/**

spring.cloud.gateway.routes[5].id=product-service
spring.cloud.gateway.routes[5].uri=lb://product
spring.cloud.gateway.routes[5].predicates[0]=Path=/product/**

spring.cloud.gateway.routes[6].id=order-service
spring.cloud.gateway.routes[6].uri=lb://order
spring.cloud.gateway.routes[6].predicates[0]=Path=/order/**

spring.cloud.gateway.routes[7].id=cart-service
spring.cloud.gateway.routes[7].uri=lb://cart
spring.cloud.gateway.routes[7].predicates[0]=Path=/cart/**

# CORS Configuration
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origins=http://localhost:4200,https://yourdomain.com
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=true

# Actuator
management.endpoints.web.exposure.include=health,metrics,gateway

# Logging
logging.level.root=INFO
logging.level.org.springframework.cloud.gateway=DEBUG
```

### Startup Command
```bash
# Standard startup
java -jar Gate/target/gate-0.0.1-SNAPSHOT.jar

# With custom Eureka URL (production)
java -Deureka.client.service-url.defaultzone=http://eureka-prod:8761/eureka \
  -Dserver.port=8888 \
  -jar Gate/target/gate-0.0.1-SNAPSHOT.jar

# With HTTPS/TLS
java -Dserver.ssl.key-store=/path/to/keystore.p12 \
  -Dserver.ssl.key-store-password=password \
  -Dserver.ssl.key-store-type=PKCS12 \
  -jar Gate/target/gate-0.0.1-SNAPSHOT.jar
```

### Health Check
```bash
# Gateway health
curl http://localhost:8888/actuator/health

# Test routing to User service
curl http://localhost:8888/user/health

# Test CORS
curl -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: POST" \
  -H "Access-Control-Request-Headers: Content-Type" \
  -X OPTIONS http://localhost:8888/user/health
```

---

## Microservices - Individual Requirements

### Common Architecture for All Microservices
Each service (User, Post, Comment, Like, Friend, Product, Order, Cart) follows this structure:

```
{ServiceName}/
├── pom.xml                          (Maven config)
├── src/
│   ├── main/
│   │   ├── java/com/app/{service}/
│   │   │   ├── controller/          (REST endpoints)
│   │   │   ├── service/             (Business logic)
│   │   │   ├── repository/          (Database access)
│   │   │   ├── entity/              (JPA entities)
│   │   │   ├── dto/                 (Data Transfer Objects)
│   │   │   ├── exception/           (Custom exceptions)
│   │   │   ├── config/              (Spring configuration)
│   │   │   └── {ServiceName}Application.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/                    (Unit tests)
├── target/
│   ├── {service}-0.0.1-SNAPSHOT.jar (Compiled JAR)
│   ├── classes/                     (Compiled classes)
│   └── ...
├── Dockerfile                       (Docker image definition)
└── HELP.md                          (Documentation)
```

### Common pom.xml Configuration

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.6</version>
    <relativePath/>
</parent>

<properties>
    <java.version>21</java.version>
    <lombok.version>1.18.30</lombok.version>
    <spring-cloud.version>2023.0.6</spring-cloud.version>
    <janino.version>3.1.10</janino.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>

    <!-- Spring Cloud -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>

    <!-- Database -->
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>

    <!-- Utility -->
    <dependency>
        <groupId>lombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
        <scope>provided</scope>
    </dependency>

    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

### Common application.properties Template

```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/
server.tomcat.threads.max=200
server.tomcat.max-connections=10000

# Application Name
spring.application.name=user-service

# Data Source (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/user_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=app_user
spring.datasource.password=secure_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Connection Pool (HikariCP)
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=true
eureka.instance.lease-renewal-interval-in-seconds=30
eureka.instance.lease-expiration-duration-in-seconds=90

# Actuator
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=always
management.metrics.export.simple.enabled=true

# Logging
logging.level.root=INFO
logging.level.com.app=DEBUG
logging.file.name=/var/log/user-service.log
logging.file.max-size=10MB
logging.file.max-history=10

# Jackson Configuration
spring.jackson.time-zone=UTC
spring.jackson.serialization.write-dates-as-timestamps=false
spring.jackson.default-property-inclusion=NON_NULL
```

### Individual Service Ports

| Service | Port | Database |
|---------|------|----------|
| User | 8081 | user_db |
| Post | 8082 | post_db |
| Comment | 8083 | comment_db |
| Like | 8084 | like_db |
| Friend | 8085 | friend_db |
| Product | 8086 | product_db |
| Order | 8087 | order_db |
| Cart | 8088 | cart_db |

### Startup Commands

```bash
# User Service
java -Dserver.port=8081 \
  -Dspring.datasource.url=jdbc:mysql://localhost:3306/user_db \
  -Dspring.datasource.username=app_user \
  -Dspring.datasource.password=password \
  -jar User/target/user-0.0.1-SNAPSHOT.jar

# Post Service
java -Dserver.port=8082 \
  -Dspring.datasource.url=jdbc:mysql://localhost:3306/post_db \
  -Dspring.datasource.username=app_user \
  -Dspring.datasource.password=password \
  -jar Post/target/post-0.0.1-SNAPSHOT.jar

# Repeat pattern for other services (Comment, Like, Friend, Product, Order, Cart)
# Just change the port and database name
```

---

## Shared Library (Utility) - Requirements

### File Location
```
backend/Utility/
├── pom.xml
├── src/
│   ├── main/
│   │   └── java/com/app/utility/  (Shared code)
│   │       ├── dto/               (DTOs shared across services)
│   │       ├── entity/            (Base entities)
│   │       ├── exception/         (Common exceptions)
│   │       ├── config/            (Shared configuration)
│   │       └── util/              (Utility classes)
│   └── test/
└── target/
    └── Utility-1.0-SNAPSHOT.jar  (Built library)
```

### Build & Installation
```bash
# IMPORTANT: Must be built FIRST before other services
cd backend/Utility
mvn clean install

# This installs the library to local Maven repository
# Output: Installing to ~/.m2/repository/com/app/utility/Utility/1.0-SNAPSHOT/
```

### Maven Configuration (pom.xml)
```xml
<modelVersion>4.0.0</modelVersion>
<groupId>com.app.utility</groupId>
<artifactId>Utility</artifactId>
<version>1.0-SNAPSHOT</version>
<packaging>jar</packaging>

<properties>
    <java.version>21</java.version>
</properties>

<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

### Dependency Usage in Microservices
```xml
<!-- In each microservice pom.xml -->
<dependency>
    <groupId>com.app.utility</groupId>
    <artifactId>Utility</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

---

## Maven Build Configuration

### Build Profiles

**Development Profile** (default):
```bash
mvn clean package
# Includes:
# - Unit tests execution
# - Code compilation
# - JAR packaging
# - Skips integration tests
```

**Production Profile**:
```bash
mvn clean package -P production -DskipTests
# Includes:
# - Optimized compilation
# - Integration tests
# - JAR packaging with minimal size
```

**CI/CD Profile**:
```bash
mvn clean package -P ci-cd -DskipTests -U
# Includes:
# - Force dependency updates (-U)
# - Skip tests for speed
# - Full compilation
```

### Build Commands

```bash
# Complete build of all backend services
#!/bin/bash
set -e

echo "Building Utility Library..."
cd backend/Utility
mvn clean install

echo "Building Eureka Service Registry..."
cd ../Eureka
mvn clean package

echo "Building API Gateway..."
cd ../Gate
mvn clean package

echo "Building Microservices..."
for service in User Post Comment Like Friend Product Order Cart; do
  echo "Building $service..."
  cd ../$service
  mvn clean package
done

echo "All services built successfully!"
echo "JAR files location: {service}/target/{service}-0.0.1-SNAPSHOT.jar"
```

### Build Troubleshooting

**Memory Issues:**
```bash
export MAVEN_OPTS="-Xmx2048m"
mvn clean package
```

**Dependency Issues:**
```bash
# Clear local repository cache
rm -rf ~/.m2/repository

# Force re-download
mvn clean package -U
```

**Utility Library Not Found:**
```bash
# Ensure Utility is built first
cd backend/Utility
mvn clean install -DskipTests
```

---

## Deployment Architecture

### Production Deployment Layout

```
Production Environment (Cloud/Server)
│
├─ Reverse Proxy (Nginx/Apache)
│  ├─ Frontend Proxy (Port 80/443)
│  └─ API Gateway Proxy (Port 8888)
│
├─ Eureka Service Registry
│  └─ Port 8761 (Internal only)
│
├─ API Gateway (Gate)
│  └─ Port 8888 (Behind Reverse Proxy)
│
├─ Microservices Cluster
│  ├─ User Service (Port 8081, Internal)
│  ├─ Post Service (Port 8082, Internal)
│  ├─ Comment Service (Port 8083, Internal)
│  ├─ Like Service (Port 8084, Internal)
│  ├─ Friend Service (Port 8085, Internal)
│  ├─ Product Service (Port 8086, Internal)
│  ├─ Order Service (Port 8087, Internal)
│  └─ Cart Service (Port 8088, Internal)
│
└─ Data Layer
   └─ MySQL Database
      ├─ user_db
      ├─ post_db
      ├─ comment_db
      ├─ like_db
      ├─ friend_db
      ├─ product_db
      ├─ order_db
      └─ cart_db
```

### Nginx Configuration Example

```nginx
# /etc/nginx/sites-available/api-gateway
upstream api_gateway {
    server localhost:8888;
    keepalive 32;
}

server {
    listen 443 ssl http2;
    server_name api.yourdomain.com;

    ssl_certificate /etc/letsencrypt/live/api.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.yourdomain.com/privkey.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    location / {
        proxy_pass http://api_gateway;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connection_http_upgrade;
        proxy_buffering off;
    }
}

# Redirect HTTP to HTTPS
server {
    listen 80;
    server_name api.yourdomain.com;
    return 301 https://$server_name$request_uri;
}
```

---

## Environment Variables - Complete Reference

### Eureka Server
```bash
EUREKA_SERVER_ENABLE_SELF_PRESERVATION=false
EUREKA_SERVER_EVICTION_INTERVAL_TIMER_IN_MS=10000
EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
EUREKA_CLIENT_FETCH_REGISTRY=false
SERVER_PORT=8761
```

### API Gateway (Gate)
```bash
SERVER_PORT=8888
SPRING_APPLICATION_NAME=gate
EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE=http://eureka:8761/eureka
SPRING_CLOUD_GATEWAY_ROUTES_0_ID=user-service
SPRING_CLOUD_GATEWAY_ROUTES_0_URI=lb://user
# ... (repeat for each service route)
```

### All Microservices (User, Post, Comment, Like, Friend, Product, Order, Cart)
```bash
# Server
SERVER_PORT=8081-8088

# Database
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/{service}_db
SPRING_DATASOURCE_USERNAME=app_user
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect
SPRING_JPA_SHOW_SQL=false

# Service Discovery
EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE=http://eureka:8761/eureka
EUREKA_INSTANCE_HOSTNAME=${HOSTNAME}
EUREKA_INSTANCE_PREFER_IP_ADDRESS=true

# Connection Pool
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20
SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE=5
SPRING_DATASOURCE_HIKARI_CONNECTION_TIMEOUT=30000

# Actuator
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,metrics

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_APP=DEBUG
```

---

## Database Setup - Complete Guide

### MySQL Installation

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install mysql-server

# macOS
brew install mysql

# Start MySQL
sudo systemctl start mysql  # Linux
brew services start mysql  # macOS

# Secure installation
mysql_secure_installation
```

### Create Application User

```sql
-- Connect as root
mysql -u root -p

-- Create application user
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'secure_password_here';
GRANT ALL PRIVILEGES ON *.* TO 'app_user'@'localhost';

-- For remote access (be careful with permissions)
CREATE USER 'app_user'@'%' IDENTIFIED BY 'secure_password_here';
GRANT ALL PRIVILEGES ON *.* TO 'app_user'@'%';

FLUSH PRIVILEGES;
```

### Create Databases

```sql
CREATE DATABASE user_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE post_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE comment_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE like_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE friend_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE product_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE order_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE cart_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Verify
SHOW DATABASES;
```

### Test Database Connectivity

```bash
# Test connection from command line
mysql -h localhost -u app_user -p user_db
# Type password and press Enter
# Should show: mysql> prompt

# Test from Spring Boot
# Add to application.properties and run:
# spring.datasource.url=jdbc:mysql://localhost:3306/user_db
# mvn spring-boot:run

# Check database logs
sudo tail -f /var/log/mysql/error.log
```

### Database Backup & Restore

```bash
# Backup all databases
mysqldump -u app_user -p --all-databases > backup-$(date +%Y%m%d).sql

# Backup specific database
mysqldump -u app_user -p user_db > user_db-$(date +%Y%m%d).sql

# Restore from backup
mysql -u app_user -p < backup-20260501.sql

# Schedule automatic daily backup (cron)
# 0 2 * * * /usr/bin/mysqldump -u app_user -p${DB_PASSWORD} --all-databases > /backups/daily-$(date +\%Y\%m\%d).sql
```

---

## Docker Containerization

### Dockerfile for Each Service

**Eureka Dockerfile:**
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
LABEL maintainer="Your Team"
WORKDIR /app
COPY target/eureka-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8761
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8761/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**API Gateway Dockerfile:**
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
LABEL maintainer="Your Team"
WORKDIR /app
COPY target/gate-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8888
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8888/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Microservice Dockerfile (User/Post/etc):**
```dockerfile
FROM eclipse-temurin:21-jdk-alpine
LABEL maintainer="Your Team"
WORKDIR /app
COPY target/user-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Build Commands

```bash
# Build individual images
cd backend/Eureka
docker build -t myapp-eureka:latest -t myapp-eureka:1.0 .

cd ../Gate
docker build -t myapp-gate:latest -t myapp-gate:1.0 .

cd ../User
docker build -t myapp-user:latest -t myapp-user:1.0 .

# Repeat for other services

# Tag for registry push
docker tag myapp-eureka:latest your-registry/myapp-eureka:latest
docker push your-registry/myapp-eureka:latest
```

### Docker Compose for Local Development

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root_password
      MYSQL_DATABASE: user_db
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  eureka:
    image: myapp-eureka:latest
    ports:
      - "8761:8761"
    depends_on:
      - mysql
    environment:
      EUREKA_SERVER_ENABLE_SELF_PRESERVATION: "false"

  gate:
    image: myapp-gate:latest
    ports:
      - "8888:8888"
    depends_on:
      - eureka
    environment:
      EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE: http://eureka:8761/eureka

  user:
    image: myapp-user:latest
    ports:
      - "8081:8081"
    depends_on:
      - eureka
      - mysql
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/user_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root_password
      EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE: http://eureka:8761/eureka

  # Repeat for other services (Post, Comment, Like, Friend, Product, Order, Cart)

volumes:
  mysql_data:
```

### Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Remove volumes (clean up)
docker-compose down -v
```

---

## Kubernetes Deployment

### Kubernetes Namespace & ConfigMap

```yaml
# namespace.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: myapp

---
# configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-config
  namespace: myapp
data:
  eureka.client.service-url.defaultzone: "http://eureka:8761/eureka"
  spring.datasource.hikari.maximum-pool-size: "20"
  logging.level.root: "INFO"
```

### Eureka Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: eureka
  namespace: myapp
spec:
  replicas: 1
  selector:
    matchLabels:
      app: eureka
  template:
    metadata:
      labels:
        app: eureka
    spec:
      containers:
      - name: eureka
        image: your-registry/myapp-eureka:latest
        ports:
        - containerPort: 8761
        env:
        - name: EUREKA_SERVER_ENABLE_SELF_PRESERVATION
          value: "false"
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8761
          initialDelaySeconds: 20
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: eureka
  namespace: myapp
spec:
  type: ClusterIP
  ports:
  - port: 8761
    targetPort: 8761
  selector:
    app: eureka
```

### API Gateway Deployment

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: gate
  namespace: myapp
spec:
  replicas: 1
  selector:
    matchLabels:
      app: gate
  template:
    metadata:
      labels:
        app: gate
    spec:
      containers:
      - name: gate
        image: your-registry/myapp-gate:latest
        ports:
        - containerPort: 8888
        env:
        - name: EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE
          value: "http://eureka:8761/eureka"
        resources:
          requests:
            memory: "1Gi"
            cpu: "500m"
          limits:
            memory: "2Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8888
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8888
          initialDelaySeconds: 20
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: gate
  namespace: myapp
spec:
  type: LoadBalancer
  ports:
  - port: 8888
    targetPort: 8888
  selector:
    app: gate
```

### Microservice Deployment Template

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user
  namespace: myapp
spec:
  replicas: 3  # Horizontal scaling
  selector:
    matchLabels:
      app: user
  template:
    metadata:
      labels:
        app: user
    spec:
      containers:
      - name: user
        image: your-registry/myapp-user:latest
        ports:
        - containerPort: 8081
        env:
        - name: SPRING_DATASOURCE_URL
          value: "jdbc:mysql://mysql:3306/user_db"
        - name: SPRING_DATASOURCE_USERNAME
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: username
        - name: SPRING_DATASOURCE_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-credentials
              key: password
        - name: EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE
          value: "http://eureka:8761/eureka"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 45
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8081
          initialDelaySeconds: 30
          periodSeconds: 5

---
apiVersion: v1
kind: Service
metadata:
  name: user
  namespace: myapp
spec:
  type: ClusterIP
  ports:
  - port: 8081
    targetPort: 8081
  selector:
    app: user
```

### Deploy to Kubernetes

```bash
# Create namespace and ConfigMap
kubectl apply -f namespace.yaml
kubectl apply -f configmap.yaml

# Deploy Eureka
kubectl apply -f eureka-deployment.yaml

# Deploy API Gateway
kubectl apply -f gate-deployment.yaml

# Deploy Microservices
kubectl apply -f user-deployment.yaml
kubectl apply -f post-deployment.yaml
# ... repeat for other services

# Check deployments
kubectl get deployments -n myapp
kubectl get pods -n myapp
kubectl get services -n myapp

# View logs
kubectl logs -n myapp deployment/user

# Scale a service
kubectl scale deployment user --replicas=5 -n myapp

# Update image
kubectl set image deployment/user user=your-registry/myapp-user:2.0 -n myapp
```

---

## Performance Tuning

### JVM Tuning Parameters

```bash
# Optimal JVM settings for production
java -Xmx1024m \
  -Xms512m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled \
  -XX:+UnlockExperimentalVMOptions \
  -XX:G1NewCollectionHeuristicPercent=35 \
  -XX:G1ReservePercent=20 \
  -jar app.jar
```

### Database Connection Pool Tuning

```properties
# For high-traffic services (Gateway, popular microservices)
spring.datasource.hikari.maximum-pool-size=30
spring.datasource.hikari.minimum-idle=10

# For low-traffic services
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=3
```

### Spring Boot Application Tuning

```properties
# Disable unused features
spring.jmx.enabled=false
spring.mvc.async.request-timeout=60000

# Enable compression
server.compression.enabled=true
server.compression.min-response-size=1024

# Thread pool tuning
server.tomcat.threads.max=400
server.tomcat.threads.min-spare=10
server.tomcat.max-connections=20000
server.tomcat.accept-count=1000
```

### MySQL Query Optimization

```sql
-- Add indexes for frequently queried columns
ALTER TABLE users ADD INDEX idx_email (email);
ALTER TABLE posts ADD INDEX idx_user_id (user_id);
ALTER TABLE comments ADD INDEX idx_post_id (post_id);

-- Monitor slow queries
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;

-- Analyze query performance
EXPLAIN SELECT * FROM users WHERE email = 'test@example.com';
```

---

## Troubleshooting Guide

### Service Won't Start

```bash
# Check Java version
java -version  # Should be 21.x.x

# Check if port is already in use
lsof -i :8081  # Check port 8081
# Or on Windows
netstat -ano | findstr :8081

# Check logs for errors
tail -f logs/application.log
```

### Database Connection Errors

```bash
# Test MySQL connectivity
mysql -h mysql-host -u app_user -p -e "SELECT 1"

# Check application.properties
grep spring.datasource application.properties

# Verify MySQL is running
sudo systemctl status mysql
```

### Eureka Registration Issues

```bash
# Check if Eureka is running
curl http://eureka-host:8761/actuator/health

# View registered services
curl http://eureka-host:8761/eureka/apps

# Check service connectivity
telnet eureka-host 8761
```

### API Gateway Routing Issues

```bash
# Test gateway endpoint
curl -v http://gateway:8888/user/health

# Check route configuration
curl http://gateway:8888/actuator/gateway/routes

# Monitor gateway logs
tail -f logs/gate.log
```

### High Memory Usage

```bash
# Check JVM heap usage
jps -l  # List Java processes
jmap -heap <pid>  # Check heap memory

# Restart service with lower heap
java -Xmx512m -Xms256m -jar app.jar
```

### Slow Response Times

```bash
# Enable query logging
SET GLOBAL general_log = 'ON';
SET GLOBAL log_output = 'TABLE';

# Check slow queries
SHOW PROCESSLIST;
SELECT * FROM mysql.general_log;

# Analyze execution plans
EXPLAIN SELECT * FROM users WHERE id = 1;
```

---

## Deployment Checklist

- [ ] JDK 21 installed on all servers
- [ ] Maven 3.8.1+ installed
- [ ] MySQL 8.0+ installed and running
- [ ] All databases created with correct charset
- [ ] Utility library built and installed
- [ ] All microservices built successfully
- [ ] Firewall rules configured
- [ ] SSL/TLS certificates installed
- [ ] Environment variables configured
- [ ] Health checks passing for all services
- [ ] Eureka showing all services registered
- [ ] API Gateway routes responding
- [ ] Frontend can reach API Gateway
- [ ] Backups configured and tested
- [ ] Monitoring and alerting enabled
- [ ] Load balancer configured
- [ ] Documentation updated

---

**Document Version:** 1.0  
**Last Updated:** May 2026  
**Maintained By:** DevOps Team
