# Complete Deployment Requirements - Project Overview

**Last Updated:** May 2026  
**Project Type:** Spring Boot Microservices + Angular Frontend  
**Current Stack:** Java 21, Spring Boot 3.2.6, Spring Cloud 2023.0.6, Angular 20, Node.js 18+

---

## Table of Contents
1. [Infrastructure Requirements](#infrastructure-requirements)
2. [System & Environment Requirements](#system--environment-requirements)
3. [Backend Services Requirements](#backend-services-requirements)
4. [Frontend Requirements](#frontend-requirements)
5. [Database Requirements](#database-requirements)
6. [Build & Compilation Requirements](#build--compilation-requirements)
7. [Deployment Configuration](#deployment-configuration)
8. [Security Requirements](#security-requirements)
9. [Performance & Scaling Requirements](#performance--scaling-requirements)
10. [Testing Requirements](#testing-requirements)
11. [Pre-Deployment Checklist](#pre-deployment-checklist)
12. [Deployment Monitoring](#deployment-monitoring)

---

## Infrastructure Requirements

### Minimum Cloud Infrastructure
- **Total Deployments:** 11 Backend Services + 1 Frontend + 1 Database
- **Recommended Platform:** Railway, AWS EC2, Google Cloud Run, or Azure Container Instances
- **Minimum RAM:** 8GB (for all services combined)
- **Minimum Storage:** 50GB (OS + services + database)
- **Network:** Public IP for frontend, private network for backend services

### Service Distribution

| Service | Type | Port | Min RAM | Min CPU |
|---------|------|------|---------|---------|
| Eureka | Service Registry | 8761 | 512MB | 0.5 |
| Gate | API Gateway | 8888 | 512MB | 0.5 |
| User | Microservice | 8081 | 512MB | 0.5 |
| Post | Microservice | 8082 | 512MB | 0.5 |
| Comment | Microservice | 8083 | 512MB | 0.5 |
| Like | Microservice | 8084 | 512MB | 0.5 |
| Friend | Microservice | 8085 | 512MB | 0.5 |
| Product | Microservice | 8086 | 512MB | 0.5 |
| Order | Microservice | 8087 | 512MB | 0.5 |
| Cart | Microservice | 8088 | 512MB | 0.5 |
| Utility | Shared Library | N/A | N/A | N/A |
| Frontend | Angular SPA | 80/443 | 256MB | 0.25 |
| MySQL | Database | 3306 | 512MB | 1 |

**Total Minimum:** 6GB RAM, 6 CPU cores

### Network Configuration
```
Internet
    |
    ├─ Frontend (Port 80/443)
    |
    └─ API Gateway/Gate (Port 8888)
         |
         ├─ Eureka Service Registry (Port 8761)
         |
         └─ Microservices (Ports 8081-8088)
              |
              └─ MySQL Database (Port 3306)
```

---

## System & Environment Requirements

### Local Development Environment
- **Operating System:** Windows 10+, macOS 11+, Linux (Ubuntu 20.04+)
- **Java Development Kit:** JDK 21 (OpenJDK or Oracle)
  - Environment Variable: `JAVA_HOME` must point to JDK 21
  - Verify: `java -version` should show Java 21.x.x
- **Apache Maven:** 3.8.1+ (for building backend)
  - Verify: `mvn -version` should show Maven 3.8.1+
- **Node.js:** 18.x LTS or higher
  - Verify: `node -version` should show v18+
  - NPM: 9.x or higher (comes with Node.js)
- **Git:** 2.30+
- **Docker:** 20.10+ (optional but recommended for local testing)

### Production Environment
- **OS:** Linux (Ubuntu 20.04 LTS or CentOS 8+) recommended
- **Java Runtime:** JDK 21 or JRE 21
- **Database:** MySQL 8.0+ (mandatory)
- **Web Server:** Nginx or Apache (for frontend reverse proxy)
- **Process Manager:** systemd, Supervisor, or PM2
- **Container Runtime:** Docker 20.10+ (if containerizing)

### CI/CD Requirements
- **Source Control:** Git with GitHub/GitLab/Bitbucket
- **CI/CD Pipeline:** GitHub Actions, GitLab CI, Jenkins, or CircleCI
- **Artifact Repository:** Maven Central or private Artifactory (optional)
- **Container Registry:** Docker Hub, GitHub Container Registry, or private registry

---

## Backend Services Requirements

### Global Backend Requirements
- **Framework:** Spring Boot 3.2.6
- **Java Version:** 21
- **Build Tool:** Maven 3.8.1+
- **Spring Cloud:** 2023.0.6 or higher
- **Spring Cloud Netflix:** Eureka client/server
- **Spring Cloud Gateway:** For API Gateway
- **Lombok:** 1.18.30 (for code generation)
- **Janino:** 3.1.10 (safe version for logging)

### Service Registry (Eureka)
**File:** `backend/Eureka/`
- **Port:** 8761
- **Purpose:** Service discovery and registration
- **Key Dependencies:**
  - `spring-cloud-starter-netflix-eureka-server`
- **Configuration Requirements:**
  - `application.properties` must enable Eureka server mode
  - Must be started before other microservices
  - Health check endpoint: `/actuator/health`

### API Gateway (Gate)
**File:** `backend/Gate/`
- **Port:** 8888
- **Purpose:** Routes requests to microservices
- **Key Dependencies:**
  - `spring-cloud-starter-netflix-eureka-client`
  - `spring-cloud-starter-gateway`
  - `spring-boot-starter-actuator`
  - `jjwt-api` 0.11.5 (JWT authentication)
- **Configuration Requirements:**
  - `EUREKA_SERVICE_URL` must point to Eureka server
  - CORS configuration for frontend origin
  - Route definitions for all microservices:
    - `/user/**` → User service
    - `/post/**` → Post service
    - `/comment/**` → Comment service
    - `/like/**` → Like service
    - `/friend/**` → Friend service
    - `/product/**` → Product service
    - `/order/**` → Order service
    - `/cart/**` → Cart service
  - JWT authentication filter
  - Environment variables: `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

### Microservices (User, Post, Comment, Like, Friend, Product, Order, Cart)
**Files:** `backend/User/`, `backend/Post/`, `backend/Comment/`, `backend/Like/`, `backend/Friend/`, `backend/Product/`, `backend/Order/`, `backend/Cart/`

**Common Requirements for All:**
- **Spring Boot:** 3.2.6
- **Java:** 21
- **Database:** MySQL 8.0+
- **ORM:** JPA/Hibernate
- **Framework:** Spring Data JPA, Spring Web
- **Service Discovery:** Eureka client registration
- **Key Dependencies (Common):**
  - `spring-boot-starter-data-jpa` - Database ORM
  - `spring-boot-starter-web` - REST API support
  - `spring-boot-starter-security` - Authentication/Authorization
  - `mysql-connector-java` 8.0.x - MySQL driver
  - `lombok` 1.18.30 - Code generation
  - `spring-cloud-starter-netflix-eureka-client` - Service discovery
  - `spring-boot-starter-actuator` - Health checks and metrics

**Environment Variables (Each Service):**
```
SPRING_DATASOURCE_URL=jdbc:mysql://<mysql-host>:3306/<service_db>
SPRING_DATASOURCE_USERNAME=<mysql_user>
SPRING_DATASOURCE_PASSWORD=<mysql_password>
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://<eureka-host>:8761/eureka
SERVER_PORT=<service_port>
SERVER_SERVLET_CONTEXT_PATH=/
```

### Shared Utility Library
**File:** `backend/Utility/`
- **Purpose:** Shared code, models, and utilities for all microservices
- **Built as:** Maven library (JAR)
- **Required for:** All microservices depend on this
- **Build Requirement:** Must be built and installed to local Maven repository before building microservices
  ```bash
  cd backend/Utility
  mvn clean install
  ```

---

## Frontend Requirements

### Development Environment
**Location:** `frontend/myapp/`

**Node.js & NPM:**
- Node.js: 18.x LTS or higher
- NPM: 9.x or higher

**Angular Version:**
- Angular: 20.3.0 or higher
- TypeScript: 5.9.2 or higher
- Angular CLI: 20.3.3 or higher

**Key Dependencies:**
- `@angular/common` ^20.3.0
- `@angular/compiler` ^20.3.0
- `@angular/core` ^20.3.0
- `@angular/forms` ^20.3.0
- `@angular/platform-browser` ^20.3.0
- `@angular/router` ^20.3.0
- `rxjs` ~7.8.0
- `tslib` ^2.3.0
- `zone.js` ~0.15.0

### Build Requirements
- **Build Tool:** Angular CLI (npm run build)
- **Build Output:** `dist/` directory (static files)
- **Build Time:** ~5-10 minutes
- **Disk Space for Build:** ~2GB temporary space

### Environment Configuration
**Configuration File:** `frontend/myapp/public/env.js`
```javascript
window.GATEWAY_API_URL = 'http://localhost:8888'; // Development
// Production: https://your-api-gateway-domain:8888
```

**Build Process:**
1. `npm install` - Install dependencies
2. `npm run set-env` - Configure environment variables
3. `npm run build` - Build production bundle
4. Output available in `dist/` directory

### Deployment Options
- **Static Hosting:** Vercel, Netlify, GitHub Pages, AWS S3 + CloudFront
- **Container Deployment:** Docker with Nginx
- **Server Deployment:** Node.js with Express, Apache, or Nginx

---

## Database Requirements

### MySQL Database
- **Version:** 8.0 or higher
- **Port:** 3306 (default)
- **Encoding:** UTF-8 or UTF-8MB4
- **Collation:** utf8mb4_unicode_ci (recommended)
- **Authentication:** MySQL 8.0 native authentication (caching_sha2_password)

### Database Schema
**Required Databases:** One database per microservice (recommended isolation)
```
mysql-host:3306/
├── user_db          (for User service)
├── post_db          (for Post service)
├── comment_db       (for Comment service)
├── like_db          (for Like service)
├── friend_db        (for Friend service)
├── product_db       (for Product service)
├── order_db         (for Order service)
└── cart_db          (for Cart service)
```

**Alternative:** Single shared database with all tables (NOT recommended for production)

### Database User Requirements
- **Username:** Application user (e.g., `app_user`)
- **Password:** Strong password (min 12 characters, alphanumeric + special chars)
- **Privileges:** 
  - CREATE, ALTER, DROP, SELECT, INSERT, UPDATE, DELETE on assigned databases
  - NOT root/admin account
- **Host Access:** Restrict to backend services network

### Database Backup Requirements
- **Backup Frequency:** Daily (minimum)
- **Retention:** 30 days (minimum)
- **Backup Method:** `mysqldump` or automated cloud backup
- **Restore Testing:** Monthly verification

### Connection Pool Configuration
```
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

---

## Build & Compilation Requirements

### Backend Build Process

**Prerequisites:**
1. JDK 21 installed and `JAVA_HOME` configured
2. Maven 3.8.1+ installed
3. MySQL database available (for integration tests)
4. All source files present in `backend/` directory

**Build Steps:**
```bash
# 1. Build Utility Library first
cd backend/Utility
mvn clean install

# 2. Build each microservice
cd ../Eureka
mvn clean package

cd ../Gate
mvn clean package

cd ../User
mvn clean package

# Repeat for: Post, Comment, Like, Friend, Product, Order, Cart
```

**Build Output:**
- Location: `{service}/target/{service}-0.0.1-SNAPSHOT.jar`
- Size: 50-150MB per JAR (varies by dependencies)

**Maven Build Configuration:**
```xml
<properties>
    <java.version>21</java.version>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

**Build Options:**
- `mvn clean package` - Standard build with tests
- `mvn clean package -DskipTests` - Skip unit tests (for CI/CD)
- `mvn clean package -U` - Force update dependencies
- `mvn clean package -X` - Debug mode

### Frontend Build Process

**Prerequisites:**
1. Node.js 18+ and NPM 9+ installed
2. All source files present in `frontend/myapp/` directory
3. Internet connection for NPM package downloads

**Build Steps:**
```bash
cd frontend/myapp

# 1. Install dependencies
npm install

# 2. Set environment variables (automatic via npm script)
npm run set-env

# 3. Build production bundle
npm run build
```

**Build Output:**
- Location: `frontend/myapp/dist/myapp/browser/`
- Size: ~5-10MB (compressed)
- Static files only (no server required)

**Build Configuration:**
```json
{
  "projects": {
    "myapp": {
      "architect": {
        "build": {
          "configurations": {
            "production": {
              "outputHashing": "all"
            }
          }
        }
      }
    }
  }
}
```

**Build Environment Variables:**
```bash
# Set before build
export GATEWAY_API_URL=https://api-gateway.yourdomain.com:8888
npm run build
```

---

## Deployment Configuration

### Environment Variables for Deployment

#### Backend Services - Eureka
```env
# Eureka Server Configuration
EUREKA_SERVER_ENABLE_SELF_PRESERVATION=false
EUREKA_CLIENT_REGISTER_WITH_EUREKA=false
EUREKA_CLIENT_FETCH_REGISTRY=false
SERVER_PORT=8761
EUREKA_INSTANCE_HOSTNAME=eureka.yourdomain.com
```

#### Backend Services - API Gateway (Gate)
```env
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/gateway_db
SPRING_DATASOURCE_USERNAME=app_user
SPRING_DATASOURCE_PASSWORD=secure_password
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-host:8761/eureka
SERVER_PORT=8888
SERVER_SERVLET_CONTEXT_PATH=/
SPRING_CLOUD_GATEWAY_ROUTES_0_ID=user-service
SPRING_CLOUD_GATEWAY_ROUTES_0_URI=lb://user
SPRING_CLOUD_GATEWAY_ROUTES_0_PREDICATES_0=Path=/user/**
# ... (repeat for each microservice)
```

#### Backend Services - Microservices (User, Post, Comment, Like, Friend, Product, Order, Cart)
```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-host:3306/{service}_db
SPRING_DATASOURCE_USERNAME=app_user
SPRING_DATASOURCE_PASSWORD=secure_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

# Service Registry
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka-host:8761/eureka
EUREKA_INSTANCE_HOSTNAME=service-hostname
EUREKA_INSTANCE_INSTANCE_ID=${EUREKA_INSTANCE_HOSTNAME}:${server.port}

# Server Configuration
SERVER_PORT=8081-8088 (different for each service)
SERVER_TOMCAT_THREADS_MAX=200
SERVER_TOMCAT_MAX_CONNECTIONS=10000

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_APP=DEBUG
LOGGING_FILE_NAME=/var/log/{service}.log
```

#### Frontend - Environment Configuration
```javascript
// public/env.js - Set before deployment
window.GATEWAY_API_URL = 'https://api.yourdomain.com:8888';
window.APP_VERSION = '1.0.0';
window.APP_ENVIRONMENT = 'production';
```

### Configuration Files

#### Backend - application.properties (per service)
```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/user_db
spring.datasource.username=app_user
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.instance.hostname=localhost

# Actuator
management.endpoints.web.exposure.include=health,metrics
management.endpoint.health.show-details=always

# Logging
logging.level.root=INFO
logging.level.com.app=DEBUG
```

---

## Security Requirements

### Authentication & Authorization
- **JWT Tokens:** Used for API authentication (jjwt-api 0.11.5)
- **Token Storage:** Secure httpOnly cookies (frontend)
- **Token Expiry:** 1 hour (configurable)
- **Refresh Token:** 7 days (configurable)

### SSL/TLS Requirements
- **Protocol:** TLS 1.2 or higher
- **Certificate:** SSL/TLS certificate for production domain
  - Self-signed for development/staging
  - CA-signed for production
- **Tools:**
  - Let's Encrypt (free, automated) - Recommended
  - AWS Certificate Manager (if using AWS)
  - Paid commercial certificates

### API Security
- **CORS:** Configured in API Gateway
  - Allowed Origins: Frontend domain only
  - Allowed Methods: GET, POST, PUT, DELETE, OPTIONS
  - Allowed Headers: Content-Type, Authorization
- **Rate Limiting:** Implement in API Gateway
  - Max 100 requests/minute per IP
  - Max 1000 requests/hour per user
- **Input Validation:** Required for all API endpoints
- **SQL Injection Protection:** Use parameterized queries (JPA handles this)
- **CSRF Protection:** Token-based CSRF protection

### Data Security
- **Database Credentials:** Stored in environment variables (NOT in code)
- **Sensitive Data:** Encrypted at rest (database encryption)
- **Data in Transit:** HTTPS/TLS for all communications
- **Password Hashing:** bcrypt or similar strong algorithm
- **API Keys:** No hardcoded secrets in source code

### Infrastructure Security
- **Firewall:** Configure firewall rules
  - Port 80/443: Open to public (frontend)
  - Port 8888: Open to frontend domain only (API Gateway)
  - Port 8081-8088: Internal only (microservices)
  - Port 8761: Internal only (Eureka)
  - Port 3306: Internal only (MySQL)
- **SSH Keys:** Key-pair authentication for server access
- **VPN/VPC:** Private network for backend services
- **Secrets Management:** Use cloud provider's secret management
  - AWS Secrets Manager
  - Azure Key Vault
  - HashiCorp Vault

### Compliance Requirements
- **Data Privacy:** GDPR/CCPA compliance if applicable
- **Logging & Auditing:** All access logged
- **Vulnerability Scanning:** Regular dependency scanning with Maven plugins
- **Security Updates:** Keep dependencies updated

---

## Performance & Scaling Requirements

### Performance Metrics
- **Target Response Time:** < 500ms for 95th percentile
- **Target Throughput:** 100+ requests/second per service
- **Target Availability:** 99.9% uptime (SLA)
- **Database Query Time:** < 100ms average

### Caching Strategy
- **HTTP Caching:** Cache static assets (frontend) - 30 days
- **API Response Caching:** 5-10 minutes for read operations
- **Database Query Caching:** Hibernate second-level cache (optional)
- **Cache Invalidation:** Event-based invalidation on updates

### Database Optimization
- **Indexes:** Foreign keys, frequently queried columns
- **Connection Pooling:** HikariCP (default in Spring Boot)
  - Max connections: 20 per service
  - Min idle: 5
- **Query Optimization:** Avoid N+1 queries, use batch processing
- **Monitoring:** Query performance metrics, slow query logs

### Vertical Scaling
**Per-Service Resources:**
- Microservices: 512MB-1GB RAM, 0.5 CPU cores
- Eureka: 512MB RAM, 0.5 CPU cores
- API Gateway: 1GB RAM, 1 CPU core
- MySQL: 2-4GB RAM, 2 CPU cores

### Horizontal Scaling
- **Load Balancing:** Nginx, HAProxy, or cloud provider LB
- **API Gateway:** Single instance required (no clustering recommended for small systems)
- **Microservices:** Can scale horizontally (multiple instances)
- **Database:** Read replicas for scaling reads (MySQL replication)
- **Frontend:** CDN for static assets (CloudFront, Cloudflare)

### Monitoring & Alerting
- **Metrics Collection:** Spring Boot Actuator, Micrometer
- **Metrics Backend:** Prometheus (recommended)
- **Visualization:** Grafana
- **Logging:** ELK Stack (Elasticsearch, Logstash, Kibana) or cloud provider
- **Alerting:** Alert on high error rates, slow responses, resource usage
- **APM:** Application Performance Monitoring (New Relic, DataDog, or Dynatrace)

---

## Testing Requirements

### Unit Testing
- **Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito
- **Coverage Target:** 80% for business logic
- **Execution:** `mvn test` in each service

### Integration Testing
- **Framework:** Spring Boot Test
- **Database Testing:** Testcontainers (recommended) or H2 in-memory DB
- **Execution:** `mvn verify`

### API Testing
- **Tool:** Postman, REST Assured, or curl
- **Test Cases:** All CRUD operations, error scenarios
- **Protocol:** REST/JSON

### Frontend Testing
- **Unit Testing:** Jasmine + Karma
- **E2E Testing:** Jasmine, Protractor, or Cypress
- **Execution:** `npm test` for unit tests
- **Coverage Target:** 80% for critical components

### Load Testing
- **Tool:** JMeter, Gatling, or Apache Bench
- **Target Load:** Simulate 100+ concurrent users
- **Duration:** 10-30 minutes per test

### Security Testing
- **Static Analysis:** SonarQube, Checkmarx
- **Dependency Scanning:** OWASP Dependency-Check, Snyk
- **Penetration Testing:** Schedule quarterly
- **Vulnerability Database:** CVE scanning for all dependencies

---

## Pre-Deployment Checklist

### Source Code
- [ ] All source code committed to Git repository
- [ ] Version tags created (e.g., v1.0.0)
- [ ] Build scripts tested locally
- [ ] No hardcoded secrets or credentials
- [ ] Code review completed
- [ ] Static code analysis passed (SonarQube)

### Backend Services
- [ ] JDK 21 installed and tested
- [ ] Maven 3.8.1+ installed and tested
- [ ] `mvn clean install` executed for Utility library
- [ ] Each service builds successfully with `mvn clean package`
- [ ] All JARs generated in `target/` directories
- [ ] Unit tests pass: `mvn test`
- [ ] Integration tests pass: `mvn verify`
- [ ] Application properties configured for production
- [ ] Environment variables documented
- [ ] Health check endpoints configured
- [ ] Logging configured for production

### Frontend
- [ ] Node.js 18+ installed and tested
- [ ] NPM 9+ installed and tested
- [ ] `npm install` completes without errors
- [ ] `npm run build` completes successfully
- [ ] `dist/` directory generated and verified
- [ ] `public/env.js` configured with production Gateway URL
- [ ] Gzip compression configured for static assets
- [ ] Browser compatibility tested (Chrome, Firefox, Safari, Edge)
- [ ] Performance metrics checked (Lighthouse score > 80)

### Database
- [ ] MySQL 8.0+ installed and running
- [ ] User database created (e.g., `user_db`)
- [ ] Post database created (e.g., `post_db`)
- [ ] All 8 microservice databases created
- [ ] Application user created with correct permissions
- [ ] Database connectivity tested from application servers
- [ ] Backup strategy implemented and tested
- [ ] Database charset set to UTF-8MB4
- [ ] Connection pool settings configured

### Infrastructure
- [ ] Cloud platform account created (Railway, AWS, GCP, Azure)
- [ ] Network/VPC configured
- [ ] Security groups/firewall rules configured
- [ ] SSH keys generated and secured
- [ ] Load balancer configured (if needed)
- [ ] SSL/TLS certificates obtained and installed
- [ ] DNS records configured
- [ ] CDN configured (if needed)
- [ ] Monitoring and alerting set up
- [ ] Log aggregation configured

### Configuration Management
- [ ] Environment variables documented in `.env.example`
- [ ] Secrets stored in secure secret manager (NOT in code)
- [ ] Configuration files reviewed for production settings
- [ ] Default ports documented (8761, 8888, 8081-8088)
- [ ] Database connection strings verified
- [ ] Service discovery (Eureka) URLs verified
- [ ] CORS configuration reviewed
- [ ] JWT token configuration set

### Documentation
- [ ] Architecture diagram created
- [ ] Deployment guide written
- [ ] Rollback procedure documented
- [ ] Troubleshooting guide prepared
- [ ] API documentation (Swagger/OpenAPI) generated
- [ ] Database schema documented
- [ ] Build and deployment commands documented
- [ ] Team onboarding guide created

### Testing
- [ ] Unit tests pass locally
- [ ] Integration tests pass on staging
- [ ] Load testing completed
- [ ] Security testing completed
- [ ] Smoke testing plan prepared
- [ ] Regression testing completed
- [ ] User acceptance testing scheduled

### Monitoring & Logging
- [ ] Centralized logging configured (ELK, Cloud Logging)
- [ ] Application metrics exported (Prometheus format)
- [ ] Health check endpoints verified
- [ ] Alert rules configured
- [ ] Dashboard created
- [ ] On-call rotation established

---

## Deployment Monitoring

### Health Checks
**Eureka Service Registry:**
```
GET http://eureka-host:8761/actuator/health
Response: { "status": "UP" }
```

**API Gateway:**
```
GET http://api-gateway-host:8888/actuator/health
Response: { "status": "UP" }
```

**Microservices:**
```
GET http://service-host:service-port/actuator/health
Response: { "status": "UP" }
```

### Service Discovery Health
```
GET http://eureka-host:8761/eureka/apps
Response: List of registered services
```

### Database Connectivity
```
MySQL: Test connection from each service
mysql -h host -u user -p -e "SELECT 1"
```

### API Testing
```
# Test API Gateway routing
curl http://api-gateway-host:8888/user/health
curl http://api-gateway-host:8888/post/health
# ... test other services
```

### Frontend Verification
```
# Test frontend deployment
curl https://frontend-domain.com
# Check response code: 200 OK
# Check index.html loads correctly
```

### Log Monitoring
- **Backend Logs:** Monitor for ERROR or WARN levels
- **Frontend Logs:** Check browser console for errors
- **Database Logs:** Monitor MySQL error log
- **System Logs:** Monitor system resource usage

### Key Metrics to Monitor
| Metric | Threshold | Alert Level |
|--------|-----------|------------|
| CPU Usage | >80% | Warning, >95% Critical |
| Memory Usage | >85% | Warning, >95% Critical |
| Disk Usage | >90% | Warning, >95% Critical |
| Response Time P95 | >500ms | Warning, >1000ms Critical |
| Error Rate | >1% | Warning, >5% Critical |
| Database Connection Pool | >18/20 | Warning |
| Eureka Instances | <Expected | Critical |

### Automated Alerts
Set up alerts for:
- Service unavailability
- High error rates (>5%)
- High latency (>1 second)
- Database connection issues
- Disk space running low (<10%)
- Memory usage critical (>95%)

---

## Quick Start Deployment Commands

### Backend Build & Deploy
```bash
# Build all services
cd backend/Utility && mvn clean install
cd ../Eureka && mvn clean package
cd ../Gate && mvn clean package
cd ../User && mvn clean package
# ... repeat for all services

# Run Eureka (standalone)
java -jar Eureka/target/eureka-0.0.1-SNAPSHOT.jar

# Run API Gateway (in another terminal)
java -Deureka.client.serviceurl.defaultzone=http://localhost:8761/eureka \
  -jar Gate/target/gate-0.0.1-SNAPSHOT.jar

# Run User service (in another terminal)
java -Dspring.datasource.url=jdbc:mysql://localhost:3306/user_db \
  -Dspring.datasource.username=root \
  -Dspring.datasource.password=password \
  -jar User/target/user-0.0.1-SNAPSHOT.jar
```

### Frontend Build & Deploy
```bash
# Build
cd frontend/myapp
npm install
npm run build

# Test locally (development)
npm start

# Serve production build
npx http-server dist/myapp/browser -p 4200
```

### Docker Deployment
```bash
# Build Docker images
docker build -t eureka:latest -f backend/Eureka/Dockerfile backend/Eureka
docker build -t gate:latest -f backend/Gate/Dockerfile backend/Gate
# ... build other services

# Run with Docker Compose
docker-compose up -d
```

---

## Support & Escalation

For deployment issues:
1. Check health endpoints: `/actuator/health`
2. Review logs in `{service}/logs/` directory
3. Verify environment variables: `env | grep SPRING_`
4. Test database connectivity: `mysql -h host -u user -p`
5. Check network connectivity: `curl http://service:port`
6. Restart affected service
7. Consult team documentation
8. Escalate to DevOps team if unresolved

---

**Document Version:** 1.0  
**Last Updated:** May 2026  
**Maintained By:** DevOps Team  
**Review Frequency:** Quarterly
