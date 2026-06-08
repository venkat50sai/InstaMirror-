# Deployment Guide: Railway + MySQL + Angular

## What this repository contains
- `frontend/myapp`: Angular UI
- `backend`: Spring Boot microservices
  - `Eureka`: service registry
  - `Gate`: API gateway
  - several service modules: `User`, `Post`, `Comment`, `Like`, `Friend`, `Product`, `Order`, `Cart`
- `Utility`: shared backend library

## What was updated for deployment
- Backend service configs now support environment variables for MySQL and Eureka endpoints.
- Gateway CORS was updated to allow non-localhost origins.
- Frontend gateway URL is now configurable through `window.GATEWAY_API_URL`.

## Railway deployment recommendation
Railway free can host a MySQL plugin and single service containers easily.
But this backend is a full Spring Cloud microservice system with many Java services.
Deploying all backend services at once on Railway free is likely not practical.

### Best realistic approach
1. Deploy the backend services on Railway only if you can run the full system in a single container or use enough services.
2. Deploy the UI as a static site on GitHub Pages, Vercel, or Railway static service.
3. Use Railway MySQL plugin for the database.

## Option 1: Recommended deployment path
This is the path you asked for: backend on Railway + static frontend on a free host.

1. Host the backend and MySQL on Railway.
   - Create a Railway project.
   - Add a MySQL plugin.
   - Deploy the backend services in the same project if possible, or as a single Docker service if service limits are reached.
2. Host the UI separately on a free static host.
   - Use Vercel, GitHub Pages, Netlify, or Railway static hosting.
   - The Angular app now loads `window.GATEWAY_API_URL` from `public/env.js`.
3. Set the production gateway URL in `public/env.js` or via a build script.
   - The app defaults to `http://localhost:8888` in development.
   - In production, `env.js` must point to the deployed gateway URL.

## Suggested deployment flow

### 1. Push code to GitHub
Railway works best if your project is in GitHub.

### 2. Create a Railway project
- Add a MySQL plugin
- Note the MySQL host, user, password, and database name

### 3. Decide backend deployment strategy
#### Option A: One Railway project per backend service (not free-friendly)
- Create services for `Eureka`, `Gate`, and each microservice.
- Each service gets its own environment variables.
- This is not ideal on Railway free due to many services.

#### Option B: One Docker service with all backend apps in a single container
- Build all modules and run them together in one container.
- This is the most Railway-friendly option for the free tier.
- It may still be heavy, but it avoids needing many separate Railway services.

### 4. Configure backend env vars
For each backend service, set these env vars on Railway:
- `SPRING_DATASOURCE_URL` → `jdbc:mysql://<mysql-host>:3306/<db_name>`
- `SPRING_DATASOURCE_USERNAME` → Railway MySQL user
- `SPRING_DATASOURCE_PASSWORD` → Railway MySQL password
- `EUREKA_SERVICE_URL` → `http://<eureka-service-host>:8761/eureka`
- `SERVER_PORT` → service port (e.g. `8081`, `8082`, ...)

### 5. Configure frontend gateway URL
The frontend now uses `window.GATEWAY_API_URL`.
The app loads this from `public/env.js`, so production must publish that file with the deployed gateway URL.
If you deploy the UI as a static site, set `GATEWAY_API_URL` when building the app or replace `public/env.js` after build.

### 6. Build the UI for production
From `frontend/myapp`:
- `npm install`
- Use one of these commands to set the gateway URL and build:
  - macOS / Linux:
    - `GATEWAY_API_URL=https://your-gateway-url npm run build`
  - Windows Command Prompt:
    - `set GATEWAY_API_URL=https://your-gateway-url&& npm run build`
  - Windows PowerShell:
    - `$env:GATEWAY_API_URL = 'https://your-gateway-url'; npm run build`

The script `npm run build` now regenerates `public/env.js` automatically.

### 7. If you use Railway for the UI
- Deploy the Angular app as a static service or Node service.
- Set `window.GATEWAY_API_URL` in the served page or host the UI on the same domain as the gateway.

## Notes on the current architecture
- The Angular app calls `http://localhost:8888` in development.
- In production, you must replace `window.GATEWAY_API_URL` with the public gateway URL.
- The gateway routes `/user/**`, `/post/**`, `/comment/**`, `/like/**`, `/friend/**`, `/product/**`, and `/cart/**`.

## Next immediate steps I can help with
1. Create a Dockerfile for the frontend and/or backend.
2. Create a Railway `railway.json` or service config file.
3. Help you decide whether to deploy the full microservice set or a simpler backend version.
4. Help you wire up the actual public gateway URL into the Angular app.

> Important: For fully free deployment, the most practical route is to host the UI separately and keep the backend as small as possible.
