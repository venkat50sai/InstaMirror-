# InstaMirror

> A production-grade, full-stack social media platform featuring microservice architecture with Spring Boot and modern Angular frontend.

## Overview

InstaMirror is a distributed social media application demonstrating enterprise-level software architecture patterns. The system implements a complete microservice ecosystem with service discovery, API gateway routing, and independent data persistence across multiple service domains.

## Architecture

### System Design

```
┌─────────────────────────────────────────────────────────────┐
│                    API Gateway (Port 8888)                  │
│                  - Request routing & load balancing         │
│                  - CORS & auth enforcement                  │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
    ┌───▼───┐  ┌────▼───┐  ┌───▼───┐
    │Service│  │Service │  │Service│  ...
    │ One   │  │ Two    │  │ Three │
    └───┬───┘  └───┬────┘  └───┬───┘
        │          │           │
    ┌───▼──────────▼───────────▼────┐
    │  Eureka Service Registry      │
    │  (Service Discovery)          │
    └───────────────────────────────┘
        │
    ┌───▼─────────────────┐
    │   MySQL Database    │
    │   (Shared Instance) │
    └─────────────────────┘
```

### Core Components

**Service Registry & Routing:**
- **Eureka Server**: Service discovery and registration
- **API Gateway**: Centralized request entry point with dynamic routing

**Business Services:**
| Service | Port | Responsibility |
|---------|------|-----------------|
| User Service | 8081 | Authentication, profiles, account management |
| Post Service | 8082 | Content creation, feed management |
| Comment Service | 8083 | Comment threads, moderation |
| Like Service | 8084 | Engagement tracking, like/unlike operations |
| Friend Service | 8085 | Connection management, relationship graph |
| Product Service | 8086 | Catalog, inventory, product metadata |
| Order Service | 8087 | Transaction processing, fulfillment |
| Cart Service | 8088 | Shopping cart, order staging |

**Data Layer:**
- **MySQL 8**: Persistent storage with schema per service pattern

**Frontend:**
- **Angular 18+**: Single-page application with responsive design

## Technology Stack

### Backend
- **Runtime**: Java 21 (LTS)
- **Framework**: Spring Boot 3.x
- **Cloud Framework**: Spring Cloud (Eureka, Gateway, Config)
- **Data Access**: Spring Data JPA, Hibernate
- **Database**: MySQL 8.x
- **Build Tool**: Maven 3.9.x
- **Container**: Docker with multi-stage builds

### Frontend
- **Framework**: Angular 18+
- **Language**: TypeScript 5.x
- **Styling**: Bootstrap 5, CSS3
- **HTTP Client**: Angular HttpClient
- **State Management**: RxJS Observables

### Infrastructure
- **Containerization**: Docker & Docker Compose
- **Cloud Platform**: Railway
- **Version Control**: Git

## Getting Started

### Prerequisites

- Java Development Kit 21+
- Node.js 18+ with npm
- MySQL 8.0+
- Maven 3.9+
- Docker & Docker Compose (optional, for containerized deployment)
- Git

### Local Development

#### Backend Setup

```bash
# Navigate to backend directory
cd backend

# Build all modules (installs dependencies & compiles)
mvn clean install -DskipTests

# Start all services (requires MySQL running)
./backend-start.sh
```

**Service Endpoints:**
- Service Registry: `http://localhost:8761`
- API Gateway: `http://localhost:8888`
- Individual Services: `http://localhost:8081-8088`

#### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend/myapp

# Install dependencies
npm install

# Start development server
npm start
```

**Access Application:**
- Frontend UI: `http://localhost:4200`
- API Endpoint: `http://localhost:8888`

### Database Initialization

For local testing, start MySQL locally and use the default credentials below:

```bash
Host: localhost
Port: 3306
User: root
Password: root
```

Create the required databases once by running:

```bash
mysql -u root -proot < database/00-create-local-databases.sql
```

Then run the service startup scripts or start the backend services with Maven. The backend services use the default local MySQL settings above, and the database scripts in the `database/` folder are also available for manual schema review.

## Deployment

### Docker Deployment (Local)

```bash
# Build backend image (includes all services in single container)
docker build -f backend/Dockerfile -t instamirror:latest .

# Start full stack with docker-compose
docker-compose up --build

# Access application
# Frontend: http://localhost
# API: http://localhost:8888
```

### Cloud Deployment (Railway)

#### Prerequisites
- Railway account at https://railway.app
- GitHub repository connected to Railway
- MySQL plugin provisioned

#### Configuration

**Root Directory:** Set to `backend/`

**Environment Variables:**
```
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DB=railway
MYSQL_USER=root
MYSQL_PASS=<your-mysql-password>
```

**Deployment Steps:**
1. Push code to GitHub main branch
2. Railway auto-detects `railway.json` and Dockerfile
3. Docker image builds automatically
4. Services deploy and start
5. Access via Railway-provided domain

## Project Structure

```
.
├── backend/
│   ├── Utility/                    # Shared libraries & utilities
│   ├── Eureka/                     # Service registry server
│   ├── Gate/                       # API Gateway
│   ├── User/                       # User microservice
│   ├── Post/                       # Post microservice
│   ├── Comment/                    # Comment microservice
│   ├── Like/                       # Like microservice
│   ├── Friend/                     # Friend microservice
│   ├── Product/                    # Product microservice
│   ├── Order/                      # Order microservice
│   ├── Cart/                       # Cart microservice
│   ├── Dockerfile                  # Multi-stage Docker build
│   ├── docker-compose.yml          # Compose manifest
│   └── backend-start.sh            # Service startup script
├── frontend/
│   └── myapp/                      # Angular SPA application
├── railway.json                    # Railway configuration
├── .gitignore                      # Git ignore rules
└── README.md                       # This file
```

## API Documentation

### Gateway Routing

All requests route through `http://[gateway-host]:8888`

**Available Routes:**
- `/user/**` → User Service (8081)
- `/post/**` → Post Service (8082)
- `/comment/**` → Comment Service (8083)
- `/like/**` → Like Service (8084)
- `/friend/**` → Friend Service (8085)
- `/product/**` → Product Service (8086)
- `/order/**` → Order Service (8087)
- `/cart/**` → Cart Service (8088)

### Service Discovery

**Eureka Registry:** `http://[eureka-host]:8761`

Service instances auto-register on startup and are available for inter-service communication via Eureka discovery.

## Configuration

### Application Properties

Services load configuration from `application.properties` in their respective `src/main/resources/` directories.

**Common Properties:**
```properties
# Server
server.port=8081
spring.application.name=user-service

# Database
spring.datasource.url=jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB}
spring.datasource.username=${MYSQL_USER}
spring.datasource.password=${MYSQL_PASS}

# Eureka Client
eureka.client.service-url.defaultZone=${EUREKA_URL}
```

## Performance Considerations

- **Build Optimization**: Tests skipped during Docker build for faster image generation
- **Multi-stage Builds**: Maven compilation isolated from runtime image
- **Service Scaling**: Individual services can scale independently
- **Database Connection Pooling**: Configured per service

## Security Notes

- Gateway enforces CORS policies for cross-origin requests
- JWT authentication via Gateway filter chain
- Services communicate internally via Eureka discovery
- MySQL credentials managed via environment variables

## Troubleshooting

**Service fails to start:**
- Verify MySQL is running and accessible
- Check environment variables are set correctly
- Review service logs in Railway Console

**Database connection errors:**
- Confirm database host, port, credentials
- Ensure database schema exists
- Check network connectivity between services

**API Gateway routing issues:**
- Verify service is registered in Eureka
- Check service is listening on expected port
- Review Gateway logs for routing errors

## Development Workflow

1. Create feature branch from `main`
2. Implement changes with tests
3. Build and test locally: `mvn clean install`
4. Push to GitHub
5. Railway auto-deploys on push to main
6. Monitor deployment logs and service health

## License

MIT License - See LICENSE file for details

---

**System Status:** Production Ready
**Last Updated:** 2026
**Deployment Target:** Railway Cloud Platform
