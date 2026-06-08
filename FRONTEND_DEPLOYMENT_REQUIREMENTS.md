# Frontend Deployment Requirements - Detailed

**Project:** Angular 20 Single Page Application (SPA)  
**Framework:** Angular 20.3.0  
**TypeScript:** 5.9.2  
**Node.js:** 18.x LTS or higher  
**NPM:** 9.x or higher  
**Last Updated:** May 2026

---

## Table of Contents
1. [Development Environment Requirements](#development-environment-requirements)
2. [Build System Requirements](#build-system-requirements)
3. [Project Structure](#project-structure)
4. [Dependencies & Versions](#dependencies--versions)
5. [Build Configuration](#build-configuration)
6. [Environment Configuration](#environment-configuration)
7. [Deployment Platforms](#deployment-platforms)
8. [Docker Containerization](#docker-containerization)
9. [Performance Optimization](#performance-optimization)
10. [Security Requirements](#security-requirements)
11. [Testing Requirements](#testing-requirements)
12. [Build & Deployment Commands](#build--deployment-commands)
13. [CI/CD Pipeline Configuration](#cicd-pipeline-configuration)
14. [Troubleshooting Guide](#troubleshooting-guide)

---

## Development Environment Requirements

### Node.js & NPM
**Minimum:**
- Node.js: 18.0.0 LTS
- NPM: 9.0.0

**Recommended for Production:**
- Node.js: 20.x LTS or higher
- NPM: 10.x or higher

**Installation:**
```bash
# macOS (using Homebrew)
brew install node@20

# Ubuntu/Debian
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs

# Windows (using Chocolatey)
choco install nodejs --version=20.0.0

# Verify installation
node --version   # v20.x.x
npm --version    # 10.x.x
```

### Angular CLI
**Required Version:** 20.3.3 or higher

```bash
# Install globally
npm install -g @angular/cli@20.3.3

# Verify installation
ng version
# Expected output:
# Angular CLI: 20.3.3
# Node: 20.x.x
# Package Manager: npm 10.x.x
```

### TypeScript
**Required Version:** 5.9.2

**Note:** TypeScript is installed as a dev dependency in the project. Do not install globally.

### Code Editor
- **VS Code** (Recommended)
- **WebStorm** (Alternative)
- **Sublime Text** (Alternative)

### Browser Requirements (Development)
- **Chrome:** Latest version
- **Firefox:** Latest version
- **Safari:** Latest version (macOS)
- **Edge:** Latest version (Windows)

---

## Build System Requirements

### Angular Build Architecture

**Build Tool:** Angular CLI (powered by Webpack/Esbuild)

**Build Configurations:**
- **Development:** Unminified, source maps included, watch mode enabled
- **Production:** Minified, optimized, source maps separate, ahead-of-time (AOT) compilation

### Build Performance
- **Development Build:** ~30-60 seconds (first build)
- **Production Build:** ~5-15 minutes (depending on dependencies)
- **Watch Mode:** ~3-10 seconds per change

### Memory Requirements
- **Development Build:** 2GB minimum
- **Production Build:** 4GB recommended
- **Watch Mode:** 2GB minimum

### Disk Space
- **Node Modules:** ~2GB
- **Build Output:** ~500MB per build
- **Source Code:** ~200MB
- **Total:** ~3GB

---

## Project Structure

```
frontend/myapp/
├── angular.json                    # Angular CLI configuration
├── package.json                    # NPM dependencies & scripts
├── package-lock.json              # Locked dependency versions
├── tsconfig.json                  # TypeScript root configuration
├── tsconfig.app.json              # TypeScript app configuration
├── tsconfig.spec.json             # TypeScript testing configuration
├── README.md                       # Project documentation
├── Dockerfile                      # Docker image definition
├── nginx.conf                      # Nginx configuration for production
│
├── public/
│   ├── index.html                 # Main HTML file (loaded via Nginx)
│   ├── env.js                     # Environment variables (generated at build)
│   ├── styles.css                 # Global styles
│   └── assets/                    # Static assets (images, fonts, etc.)
│
├── src/
│   ├── main.ts                    # Application bootstrap file
│   ├── index.html                 # Root HTML file
│   ├── styles.scss                # Global styles
│   │
│   ├── app/
│   │   ├── app.component.ts       # Root component
│   │   ├── app.component.html     # Root template
│   │   ├── app.routes.ts          # Route configuration
│   │   │
│   │   ├── core/                  # Core services & components
│   │   │   ├── services/
│   │   │   │   ├── api.service.ts          # API communication
│   │   │   │   └── auth.service.ts         # Authentication
│   │   │   ├── guards/
│   │   │   │   └── auth.guard.ts           # Route protection
│   │   │   └── interceptors/
│   │   │       └── auth.interceptor.ts     # HTTP interceptor
│   │   │
│   │   ├── shared/                # Shared components & utilities
│   │   │   ├── components/
│   │   │   │   ├── header/
│   │   │   │   ├── footer/
│   │   │   │   └── ...
│   │   │   ├── directives/
│   │   │   ├── pipes/
│   │   │   └── models/
│   │   │
│   │   ├── features/              # Feature modules
│   │   │   ├── users/
│   │   │   ├── posts/
│   │   │   ├── products/
│   │   │   └── ...
│   │   │
│   │   └── layouts/               # Layout components
│   │
│   └── environments/              # Environment configuration files
│       ├── environment.ts         # Development environment
│       └── environment.prod.ts    # Production environment
│
├── scripts/
│   └── set-env.js                 # Generate env.js from env variables
│
└── .angular/
    └── cache/                     # Angular CLI cache (auto-generated)
```

---

## Dependencies & Versions

### Core Dependencies (Production)

```json
{
  "dependencies": {
    "@angular/common": "^20.3.0",
    "@angular/compiler": "^20.3.0",
    "@angular/core": "^20.3.0",
    "@angular/forms": "^20.3.0",
    "@angular/platform-browser": "^20.3.0",
    "@angular/platform-browser-dynamic": "^20.3.0",
    "@angular/router": "^20.3.0",
    "rxjs": "~7.8.0",
    "tslib": "^2.3.0",
    "zone.js": "~0.15.0"
  }
}
```

**Angular Package Descriptions:**
- `@angular/common` - Common Angular directives & utilities
- `@angular/compiler` - Angular template compiler
- `@angular/core` - Core Angular framework
- `@angular/forms` - Form handling (reactive & template-driven)
- `@angular/platform-browser` - Browser platform services
- `@angular/platform-browser-dynamic` - Browser platform with dynamic compilation
- `@angular/router` - Client-side routing

**Third-party Dependencies:**
- `rxjs` - Reactive extensions for JavaScript (Observable library)
- `tslib` - TypeScript runtime library
- `zone.js` - Polyfill for Zone API

### Development Dependencies (Optional)

```json
{
  "devDependencies": {
    "@angular/build": "^20.3.3",
    "@angular/cli": "^20.3.3",
    "@angular/compiler-cli": "^20.3.0",
    "@types/jasmine": "~5.1.0",
    "jasmine-core": "~5.9.0",
    "karma": "~6.4.0",
    "karma-chrome-launcher": "~3.2.0",
    "karma-coverage": "~2.2.0",
    "karma-jasmine": "~5.1.0",
    "karma-jasmine-html-reporter": "~2.1.0",
    "typescript": "~5.9.2"
  }
}
```

**Testing Dependencies:**
- `jasmine-core` - JavaScript testing framework
- `karma` - Test runner
- `karma-chrome-launcher` - Karma plugin for Chrome
- `karma-coverage` - Code coverage reporting
- `karma-jasmine` - Karma adapter for Jasmine

### Version Lock Policy

**Do NOT update versions without testing:**
- Use `npm ci` in CI/CD (install locked versions)
- Use `npm install` only during development
- Always run tests after updating dependencies

---

## Build Configuration

### angular.json Configuration

**Key Build Settings:**
```json
{
  "projects": {
    "myapp": {
      "projectType": "application",
      "root": "",
      "sourceRoot": "src",
      "prefix": "app",
      "architect": {
        "build": {
          "builder": "@angular/build:browser",
          "options": {
            "outputPath": "dist/myapp/browser",
            "index": "src/index.html",
            "main": "src/main.ts",
            "polyfills": ["zone.js"],
            "tsConfig": "tsconfig.app.json",
            "assets": [
              "src/favicon.ico",
              "src/assets"
            ],
            "styles": [
              "src/styles.scss"
            ],
            "scripts": []
          },
          "configurations": {
            "production": {
              "budgets": [
                {
                  "type": "initial",
                  "maximumWarning": "500kb",
                  "maximumError": "1mb"
                },
                {
                  "type": "anyComponentStyle",
                  "maximumWarning": "2kb",
                  "maximumError": "4kb"
                }
              ],
              "outputHashing": "all",
              "optimization": true,
              "buildOptimizer": true,
              "sourceMap": false,
              "namedChunks": false,
              "aot": true,
              "extractLicenses": true,
              "vendorChunk": false
            },
            "development": {
              "buildOptimizer": false,
              "optimization": false,
              "vendorChunk": true,
              "extractLicenses": false,
              "sourceMap": true,
              "namedChunks": true
            }
          }
        },
        "serve": {
          "builder": "@angular/build:dev-server",
          "configurations": {
            "production": {
              "buildTarget": "myapp:build:production"
            },
            "development": {
              "buildTarget": "myapp:build:development"
            }
          }
        },
        "test": {
          "builder": "@angular/build:karma",
          "options": {
            "polyfills": ["zone.js", "zone.js/testing"],
            "tsConfig": "tsconfig.spec.json",
            "inlineStyleLanguage": "scss",
            "assets": [
              "src/favicon.ico",
              "src/assets"
            ],
            "styles": [
              "src/styles.scss"
            ],
            "scripts": []
          }
        }
      }
    }
  }
}
```

### Build Optimization Settings

**Production Build Optimizations:**
- Minification: JavaScript & CSS
- Tree-shaking: Remove unused code
- Code splitting: Separate bundles
- Ahead-of-Time (AOT) Compilation
- Differential loading: Modern vs. legacy browsers
- Source maps: Separate files (not included in build)

**Bundle Size Targets:**
- Main bundle: < 500KB
- Component styles: < 2KB each
- Total gzipped: < 200KB (without assets)

---

## Environment Configuration

### Environment Variables

**Development Environment** (`src/environments/environment.ts`):
```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8888',
  apiTimeout: 30000,
  logLevel: 'debug'
};
```

**Production Environment** (`src/environments/environment.prod.ts`):
```typescript
export const environment = {
  production: true,
  apiUrl: 'https://api.yourdomain.com:8888',
  apiTimeout: 30000,
  logLevel: 'error'
};
```

### Runtime Configuration (public/env.js)

**Generated at Build Time:**
```javascript
// This file is automatically generated by npm run build
window.GATEWAY_API_URL = 'https://api.yourdomain.com:8888';
window.APP_VERSION = '1.0.0';
window.APP_ENVIRONMENT = 'production';
window.ENABLE_LOGGING = false;
```

**Generation Script** (`scripts/set-env.js`):
```javascript
#!/usr/bin/env node

const fs = require('fs');
const path = require('path');

const apiUrl = process.env.GATEWAY_API_URL || 'http://localhost:8888';
const appVersion = process.env.APP_VERSION || '0.0.1';
const environment = process.env.APP_ENVIRONMENT || 'development';

const envContent = `
window.GATEWAY_API_URL = '${apiUrl}';
window.APP_VERSION = '${appVersion}';
window.APP_ENVIRONMENT = '${environment}';
`;

const envPath = path.join(__dirname, '../public/env.js');
fs.writeFileSync(envPath, envContent);
console.log(`Environment configuration written to ${envPath}`);
```

### Using Runtime Configuration in App

**Service** (`src/app/core/services/config.service.ts`):
```typescript
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  getApiUrl(): string {
    return (window as any).GATEWAY_API_URL || 'http://localhost:8888';
  }

  getAppVersion(): string {
    return (window as any).APP_VERSION || '0.0.1';
  }

  isProduction(): boolean {
    return (window as any).APP_ENVIRONMENT === 'production';
  }
}
```

**API Service** (`src/app/core/services/api.service.ts`):
```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ConfigService } from './config.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = this.configService.getApiUrl();

  constructor(
    private http: HttpClient,
    private configService: ConfigService
  ) {}

  getUsers() {
    return this.http.get(`${this.apiUrl}/user/all`);
  }

  getPosts() {
    return this.http.get(`${this.apiUrl}/post/all`);
  }
}
```

---

## Deployment Platforms

### Option 1: Vercel (Recommended for SPA)
**Pros:**
- Free tier available
- Automatic HTTPS
- Git integration (auto-deploy on push)
- Serverless functions optional
- CDN included
- Fast, simple deployment

**Setup:**
```bash
# Install Vercel CLI
npm i -g vercel

# Deploy
vercel deploy --prod

# Set environment variables
vercel env add GATEWAY_API_URL https://api.yourdomain.com:8888
```

**Configuration** (`vercel.json`):
```json
{
  "buildCommand": "npm run build",
  "outputDirectory": "dist/myapp/browser",
  "env": {
    "GATEWAY_API_URL": "@gateway_api_url"
  }
}
```

### Option 2: Netlify
**Pros:**
- Generous free tier
- Git integration
- Pre-built deploy previews
- Serverless functions included
- Form handling built-in

**Setup:**
```bash
# Install Netlify CLI
npm i -g netlify-cli

# Deploy
netlify deploy --prod --dir=dist/myapp/browser
```

**Configuration** (`netlify.toml`):
```toml
[build]
  command = "npm run build"
  publish = "dist/myapp/browser"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200

[env]
  GATEWAY_API_URL = "https://api.yourdomain.com:8888"
```

### Option 3: GitHub Pages (Free)
**Pros:**
- Completely free
- No external dependencies
- Git integration
- HTTPS included

**Cons:**
- Public repository required
- Domain limited to username.github.io

**Setup:**
```bash
# Build with base href
ng build --base-href=/myapp/ --configuration production

# Install gh-pages package
npm install --save-dev gh-pages

# Deploy
npx gh-pages -d dist/myapp/browser
```

### Option 4: AWS S3 + CloudFront
**Pros:**
- Scalable
- Fast global CDN
- Pay-as-you-go
- Advanced features

**Setup:**
```bash
# Build
npm run build

# Deploy to S3
aws s3 sync dist/myapp/browser s3://mybucket --delete

# Invalidate CloudFront
aws cloudfront create-invalidation --distribution-id E1234 --paths "/*"
```

### Option 5: Docker + Container Registry
**Best for:** Running on custom servers or Kubernetes

**Deployment Steps:**
```bash
# Build Docker image
docker build -t myapp:latest .

# Tag for registry
docker tag myapp:latest your-registry/myapp:latest

# Push to registry
docker push your-registry/myapp:latest

# Run container
docker run -p 80:80 your-registry/myapp:latest
```

### Option 6: Railway (Combined Frontend + Backend)
**Pros:**
- Integrated database hosting
- Git integration
- Environment variables UI
- Same platform for frontend & backend

**Setup:**
```bash
# Connect Railway account
railway login

# Deploy
railway up

# Set environment
railway env GATEWAY_API_URL https://api.yourdomain.com:8888
```

---

## Docker Containerization

### Dockerfile (Multi-stage Build - Recommended)

```dockerfile
# Stage 1: Build Angular app
FROM node:20-alpine AS build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Set environment variables at build time
ARG GATEWAY_API_URL=http://localhost:8888
ARG APP_VERSION=1.0.0
ARG APP_ENVIRONMENT=production

ENV GATEWAY_API_URL=$GATEWAY_API_URL
ENV APP_VERSION=$APP_VERSION
ENV APP_ENVIRONMENT=$APP_ENVIRONMENT

# Build production bundle
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:latest-alpine

# Copy Nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy built app from previous stage
COPY --from=build /app/dist/myapp/browser /usr/share/nginx/html

# Copy env.js
COPY --from=build /app/public/env.js /usr/share/nginx/html/env.js

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost/health || exit 1

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
```

### Nginx Configuration (nginx.conf)

```nginx
user nginx;
worker_processes auto;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

events {
    worker_connections 1024;
    use epoll;
    multi_accept on;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    log_format main '$remote_addr - $remote_user [$time_local] "$request" '
                    '$status $body_bytes_sent "$http_referer" '
                    '"$http_user_agent" "$http_x_forwarded_for"';

    access_log /var/log/nginx/access.log main;

    # Performance optimizations
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;
    client_max_body_size 20M;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_proxied any;
    gzip_comp_level 6;
    gzip_types text/plain text/css text/xml text/javascript
               application/json application/javascript application/xml+rss
               application/atom+xml image/svg+xml;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "no-referrer-when-downgrade" always;

    upstream backend {
        server localhost:8888;
        keepalive 32;
    }

    # HTTP redirect to HTTPS
    server {
        listen 80;
        server_name _;

        # Health check endpoint
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }

        # API redirect to backend
        location /api/ {
            return 301 https://$host$request_uri;
        }

        # All other traffic redirect to HTTPS
        location / {
            return 301 https://$host$request_uri;
        }
    }

    # HTTPS server (production only)
    server {
        listen 443 ssl http2;
        server_name yourdomain.com;

        ssl_certificate /etc/nginx/certs/fullchain.pem;
        ssl_certificate_key /etc/nginx/certs/privkey.pem;

        ssl_protocols TLSv1.2 TLSv1.3;
        ssl_ciphers HIGH:!aNULL:!MD5;
        ssl_prefer_server_ciphers on;
        ssl_session_cache shared:SSL:10m;
        ssl_session_timeout 10m;

        # Serve Angular app
        location / {
            root /usr/share/nginx/html;
            try_files $uri $uri/ /index.html;

            # Cache busting for index.html
            add_header Cache-Control "no-cache, no-store, must-revalidate" always;

            # Cache static assets
            location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
                expires 365d;
                add_header Cache-Control "public, immutable";
                access_log off;
            }

            # Load environment configuration
            location = /env.js {
                expires 1h;
                add_header Cache-Control "public, must-revalidate";
            }
        }

        # Proxy API requests to backend
        location /api/ {
            proxy_pass http://backend;
            proxy_http_version 1.1;
            proxy_set_header Upgrade $http_upgrade;
            proxy_set_header Connection 'upgrade';
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_cache_bypass $http_upgrade;
        }

        # Deny access to hidden files
        location ~ /\. {
            deny all;
            access_log off;
            log_not_found off;
        }
    }
}
```

### Docker Build & Push

```bash
# Build image with build arguments
docker build \
  --build-arg GATEWAY_API_URL=https://api.yourdomain.com:8888 \
  --build-arg APP_VERSION=1.0.0 \
  --build-arg APP_ENVIRONMENT=production \
  -t myapp:latest \
  -t myapp:1.0.0 .

# Tag for registry
docker tag myapp:latest your-registry/myapp:latest
docker tag myapp:1.0.0 your-registry/myapp:1.0.0

# Push to registry
docker push your-registry/myapp:latest
docker push your-registry/myapp:1.0.0

# Run locally (testing)
docker run -p 80:80 -e GATEWAY_API_URL=http://localhost:8888 myapp:latest
```

---

## Performance Optimization

### Bundle Size Analysis

```bash
# Analyze bundle size
ng build --stats-json
webpack-bundle-analyzer dist/myapp/browser/stats.json

# Expected sizes (gzipped):
# main.js: < 100KB
# vendor.js: < 150KB
# Total: < 250KB (excluding assets)
```

### Code Splitting

**Lazy Loading Routes:**
```typescript
// app.routes.ts
export const routes: Routes = [
  {
    path: 'users',
    loadComponent: () => import('./features/users/users.component')
      .then(m => m.UsersComponent)
  },
  {
    path: 'posts',
    loadComponent: () => import('./features/posts/posts.component')
      .then(m => m.PostsComponent)
  }
];
```

### Image Optimization

```typescript
// Use Angular's image optimization
import { NgOptimizedImage } from '@angular/common';

@Component({
  selector: 'app-header',
  template: `
    <img ngSrc="assets/logo.png"
         alt="Logo"
         width="200"
         height="50"
         priority />
  `,
  standalone: true,
  imports: [NgOptimizedImage]
})
export class HeaderComponent {}
```

### Change Detection Strategy

```typescript
import { Component, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-product-card',
  template: `<div>{{ product.name }}</div>`,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductCardComponent {
  @Input() product: Product;
}
```

### HTTP Caching

```typescript
import { HttpClient, HttpHeaders } from '@angular/common/http';

// Request caching
const headers = new HttpHeaders({
  'Cache-Control': 'max-age=3600'
});

this.http.get('/api/data', { headers }).subscribe(data => {
  // Cached for 1 hour
});
```

---

## Security Requirements

### HTTPS/TLS
- **Protocol:** TLS 1.2 or higher
- **Certificate:** SSL/TLS certificate (Let's Encrypt for free)
- **HSTS:** Enable HTTP Strict Transport Security

### Content Security Policy

```html
<!-- index.html -->
<meta http-equiv="Content-Security-Policy" 
      content="default-src 'self'; 
               script-src 'self' 'unsafe-inline'; 
               style-src 'self' 'unsafe-inline'; 
               img-src 'self' data: https:; 
               font-src 'self'; 
               connect-src 'self' https://api.yourdomain.com:8888;">
```

### CORS Configuration

**Backend (API Gateway) must allow frontend origin:**
```properties
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-origins=https://yourdomain.com
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.cloud.gateway.globalcors.cors-configurations.[/**].allowed-headers=*
spring.cloud.gateway.globalcors.cors-configurations.[/**].allow-credentials=true
```

### Authentication
- **Method:** JWT tokens in Authorization header
- **Storage:** HttpOnly, Secure cookies (NOT localStorage)
- **Token Expiry:** 1 hour
- **Refresh Token:** 7 days

### Input Validation
- Always validate user input on frontend
- Always re-validate on backend
- Use Angular forms with validation

### XSS Protection
```typescript
import { DomSanitizer } from '@angular/platform-browser';

constructor(private sanitizer: DomSanitizer) {}

getSafeHtml(html: string) {
  return this.sanitizer.sanitize(SecurityContext.HTML, html);
}
```

---

## Testing Requirements

### Unit Testing

**Framework:** Jasmine + Karma

```bash
# Run unit tests
npm test

# Run with code coverage
ng test --code-coverage

# Run in headless mode (CI/CD)
ng test --watch=false --code-coverage --browsers=ChromeHeadless

# Coverage thresholds
# Statements: > 80%
# Branches: > 75%
# Functions: > 80%
# Lines: > 80%
```

**Test File Example** (`src/app/services/api.service.spec.ts`):
```typescript
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch users', () => {
    const mockUsers = [{ id: 1, name: 'John' }];
    
    service.getUsers().subscribe(users => {
      expect(users).toEqual(mockUsers);
    });

    const req = httpMock.expectOne('/api/users');
    expect(req.request.method).toBe('GET');
    req.flush(mockUsers);
  });
});
```

### E2E Testing (Optional)

**Framework:** Jasmine or Cypress

```bash
# Run E2E tests
ng e2e

# Or with Cypress
npx cypress run
```

### Performance Testing

```bash
# Lighthouse audit
npm install -g lighthouse
lighthouse https://yourdomain.com --view

# Expected scores:
# Performance: > 90
# Accessibility: > 90
# Best Practices: > 90
# SEO: > 90
```

---

## Build & Deployment Commands

### Development Workflow

```bash
# Install dependencies
npm install

# Start development server
npm start
# Accessible at http://localhost:4200

# Build for development
npm run build

# Run unit tests
npm test

# Run unit tests with coverage
npm run test:coverage
```

### Production Build Workflow

```bash
# 1. Install dependencies
npm ci

# 2. Set environment variables
export GATEWAY_API_URL=https://api.yourdomain.com:8888
export APP_VERSION=1.0.0
export APP_ENVIRONMENT=production

# 3. Build for production
npm run build

# 4. Output location
# dist/myapp/browser/

# 5. Verify build output
ls -lh dist/myapp/browser/
# Should contain:
# - index.html (entry point)
# - main.*.js (application code)
# - env.js (environment configuration)
# - assets/ (static files)

# 6. Test production build locally
npx http-server dist/myapp/browser -p 8080
# Visit http://localhost:8080
```

### Docker Build & Deploy

```bash
# Build Docker image
docker build \
  --build-arg GATEWAY_API_URL=https://api.yourdomain.com:8888 \
  -t myapp:latest .

# Run container
docker run -p 80:80 myapp:latest

# Push to registry
docker tag myapp:latest your-registry/myapp:latest
docker push your-registry/myapp:latest

# Deploy to Kubernetes
kubectl set image deployment/frontend frontend=your-registry/myapp:latest
```

### Vercel Deployment

```bash
# Install Vercel CLI
npm i -g vercel

# Deploy to Vercel
vercel deploy --prod

# Set environment variables
vercel env add GATEWAY_API_URL https://api.yourdomain.com:8888

# View deployment logs
vercel logs
```

---

## CI/CD Pipeline Configuration

### GitHub Actions

`.github/workflows/deploy.yml`:
```yaml
name: Build & Deploy Frontend

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest

    strategy:
      matrix:
        node-version: [20.x]

    steps:
    - uses: actions/checkout@v3

    - name: Use Node.js ${{ matrix.node-version }}
      uses: actions/setup-node@v3
      with:
        node-version: ${{ matrix.node-version }}
        cache: 'npm'

    - name: Install dependencies
      run: npm ci

    - name: Run tests
      run: npm test -- --watch=false --code-coverage --browsers=ChromeHeadless

    - name: Build production
      run: npm run build
      env:
        GATEWAY_API_URL: ${{ secrets.GATEWAY_API_URL }}
        APP_VERSION: ${{ github.ref_name }}-${{ github.run_number }}

    - name: Upload coverage to Codecov
      uses: codecov/codecov-action@v3
      with:
        files: ./coverage/lcov.info

    - name: Deploy to Vercel
      if: github.event_name == 'push' && github.ref == 'refs/heads/main'
      uses: vercel/action@master
      with:
        vercel-token: ${{ secrets.VERCEL_TOKEN }}
        vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
        vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
        prod: true
```

### GitLab CI

`.gitlab-ci.yml`:
```yaml
image: node:20-alpine

stages:
  - install
  - test
  - build
  - deploy

cache:
  paths:
    - node_modules/

install:
  stage: install
  script:
    - npm ci

test:
  stage: test
  script:
    - npm run test -- --watch=false --code-coverage --browsers=ChromeHeadless
  artifacts:
    paths:
      - coverage/

build:
  stage: build
  script:
    - npm run build
  artifacts:
    paths:
      - dist/
  environment:
    name: production
    variables:
      GATEWAY_API_URL: $GATEWAY_API_URL

deploy:
  stage: deploy
  script:
    - npm i -g vercel
    - vercel deploy --prod --token $VERCEL_TOKEN
  only:
    - main
```

---

## Troubleshooting Guide

### Build Errors

**"Cannot find module" error:**
```bash
# Clear npm cache and reinstall
rm -rf node_modules package-lock.json
npm cache clean --force
npm install
npm run build
```

**"TypeScript compilation error":**
```bash
# Check TypeScript version
npx tsc --version

# Should be 5.9.2
# If not, reinstall
npm install --save-dev typescript@5.9.2
```

**"Out of memory" during build:**
```bash
# Increase Node.js memory
export NODE_OPTIONS=--max_old_space_size=4096
npm run build
```

### Runtime Errors

**"API requests failing":**
```typescript
// Check environment configuration
console.log(window.GATEWAY_API_URL);
// Should show correct API URL

// Verify CORS headers
// Open browser DevTools > Network tab
// Check Response headers for Access-Control-Allow-Origin
```

**"Assets not loading":**
```bash
# Check if assets are in dist folder
ls dist/myapp/browser/assets/

# Verify nginx configuration
# Check that assets path is correct in nginx.conf
```

**"Blank page on load":**
```typescript
// Check browser console for errors
// Verify main.ts loads successfully
// Check index.html is served correctly

// Test locally
npx http-server dist/myapp/browser
# Check that page loads
```

### Deployment Issues

**"Application not accessible after deployment":**
1. Verify DNS records point to correct IP
2. Check SSL/TLS certificate is valid
3. Verify Nginx/web server is running
4. Check firewall allows port 80/443
5. Review application logs

**"Environment variables not loading":**
```bash
# Verify env.js is in dist folder
ls dist/myapp/browser/env.js

# Check that env.js has correct content
cat dist/myapp/browser/env.js

# If missing, run set-env script
npm run set-env
```

**"API calls timing out":**
1. Verify backend service is running
2. Check network connectivity between frontend & backend
3. Verify API Gateway is routing correctly
4. Check database connections
5. Monitor backend logs for errors

---

## Monitoring & Analytics

### Application Performance Monitoring

**Install APM library:**
```bash
npm install elastic-apm-js-rms
```

**Configure in main.ts:**
```typescript
import { ServiceWorkerModule } from '@angular/service-worker';
import { elasticApm } from 'elastic-apm-js-rms';

elasticApm.init({
  serviceName: 'my-angular-app',
  serverUrl: 'https://apm.yourdomain.com'
});
```

### Error Tracking

**Install Sentry:**
```bash
npm install @sentry/angular

# Initialize in main.ts
import * as Sentry from '@sentry/angular';

Sentry.init({
  dsn: 'https://key@sentry.io/projectid',
  environment: 'production',
  tracesSampleRate: 0.1,
});
```

### Google Analytics

**Install:**
```bash
npm install @angular/google-analytics

# Configure in app.config.ts
import { provideRouter } from '@angular/router';
import { GOOGLE_ANALYTICS_PROVIDER } from '@angular/google-analytics';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    GOOGLE_ANALYTICS_PROVIDER({
      trackingId: 'GA-XXXXXXXXX-X'
    })
  ]
};
```

---

## Deployment Checklist

**Pre-Deployment:**
- [ ] Node.js 18+ and NPM 9+ installed
- [ ] All dependencies installed (`npm ci`)
- [ ] Unit tests passing (`npm test`)
- [ ] Code coverage > 80%
- [ ] Build successful (`npm run build`)
- [ ] No TypeScript errors
- [ ] No console errors/warnings
- [ ] Production bundle size acceptable
- [ ] Environment variables set
- [ ] API endpoint verified

**Deployment:**
- [ ] Correct API Gateway URL configured
- [ ] Docker image built (if using Docker)
- [ ] SSL/TLS certificate valid
- [ ] DNS records configured
- [ ] Nginx/web server configured
- [ ] Backend services running
- [ ] Database connectivity verified
- [ ] Firewall rules updated

**Post-Deployment:**
- [ ] Application accessible on production domain
- [ ] HTTPS redirect working
- [ ] API calls successful
- [ ] Static assets loading
- [ ] Images and fonts loading
- [ ] No console errors in browser
- [ ] Performance acceptable
- [ ] Monitoring & logging active
- [ ] Backup & recovery procedures tested

---

## Quick Reference Commands

```bash
# Development
npm install          # Install dependencies
npm start           # Start dev server (localhost:4200)
npm test            # Run unit tests
npm run build       # Build for production

# Production Build
GATEWAY_API_URL=https://api.yourdomain.com:8888 npm run build

# Docker
docker build -t myapp:latest .
docker run -p 80:80 myapp:latest

# Deploy
vercel deploy --prod        # Vercel
netlify deploy --prod       # Netlify
gh-pages -d dist/myapp/browser  # GitHub Pages

# Troubleshooting
npm cache clean --force
rm -rf node_modules
npm ci               # Clean install
ng build --stats-json  # Analyze bundle
```

---

**Document Version:** 1.0  
**Last Updated:** May 2026  
**Maintained By:** Frontend Development Team
