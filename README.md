# InstaMirror

A full-stack social media platform built with **Spring Boot microservices** and **Angular**.

## 🏗️ Architecture

### Backend
- **Eureka**: Service Registry for microservice discovery
- **API Gateway**: Central entry point for all client requests
- **Microservices**:
  - User Service: User authentication, profiles, and management
  - Post Service: Create and manage posts
  - Comment Service: Comment functionality
  - Like Service: Like/unlike posts and comments
  - Friend Service: Friend requests and connections
  - Product Service: Product catalog management
  - Order Service: Order processing
  - Cart Service: Shopping cart functionality

### Frontend
- **Angular**: Modern SPA for responsive UI
- Communicates with backend via Gateway API

### Database
- **MySQL**: Persistent data storage

## 📋 Tech Stack

**Backend:**
- Java 21
- Spring Boot 3.x
- Spring Cloud (Eureka, Gateway)
- Spring Data JPA
- MySQL 8

**Frontend:**
- Angular 18+
- TypeScript
- Bootstrap/CSS

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Node.js 18+
- MySQL 8+
- Maven 3.9+
- npm

### Backend Setup

```bash
cd backend

# Build all modules
mvn clean install -DskipTests

# Start services (requires MySQL)
./backend-start.sh
```

Services will be available at:
- Eureka: http://localhost:8761
- API Gateway: http://localhost:8888

### Frontend Setup

```bash
cd frontend/myapp

# Install dependencies
npm install

# Start development server
npm start
```

Visit http://localhost:4200 in your browser.

## 🐳 Docker Deployment

```bash
# Build backend image (includes all services)
docker build -f backend/Dockerfile -t instamirror-backend .

# Run with docker-compose (includes MySQL)
docker-compose up
```

## 🚂 Railway Deployment

1. Connect your GitHub repository to Railway
2. Add MySQL service plugin
3. Configure environment variables:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
4. Deploy backend service with Dockerfile

## 📁 Project Structure

```
instamirror/
├── backend/                 # Spring Boot microservices
│   ├── Eureka/             # Service registry
│   ├── Gate/               # API Gateway
│   ├── User/               # User microservice
│   ├── Post/               # Post microservice
│   ├── Comment/            # Comment microservice
│   ├── Like/               # Like microservice
│   ├── Friend/             # Friend microservice
│   ├── Product/            # Product microservice
│   ├── Order/              # Order microservice
│   ├── Cart/               # Cart microservice
│   ├── Utility/            # Shared utilities
│   ├── Dockerfile          # Multi-stage build for all services
│   ├── docker-compose.yml  # Local development setup
│   └── backend-start.sh    # Startup script
├── frontend/
│   └── myapp/              # Angular application
├── .gitignore
└── README.md
```

## 🔧 Environment Variables

### MySQL
```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/instamirror
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password
```

### Service Discovery
```
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://eureka:8761/eureka
```

## 📝 License

This project is licensed under the MIT License.

## 👥 Contributors

- Venkat (venkat50sai)

---

For deployment documentation, see the Railway documentation or check your deployment platform's guidelines.
