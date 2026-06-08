# Deploying the single-container backend to a free host (Railway / Fly.io)

This document explains how to deploy the `backend` as a single Docker container (the `backend/Dockerfile` and `backend/backend-start.sh` added here) to a free cloud host. Railway is recommended because the repository's docs reference it; Fly.io is an alternative.

Prerequisites
- Push your code to a GitHub repo.
- Install the host CLI locally when needed: `railway` or `flyctl`.
- Create accounts on Railway (https://railway.app) or Fly (https://fly.io).

High-level steps (Railway)

1. Commit the new files and push to GitHub.
2. Create a Railway project and add a MySQL plugin (or use PlanetScale for a managed MySQL).
3. In Railway, create a new Service and choose "Deploy from GitHub" pointing to this repository.
4. Set environment variables for the Railway service:
   - `MYSQL_HOST` (Railway plugin host)
   - `MYSQL_PORT` (usually `3306`)
   - `MYSQL_DB` (e.g., `instagram_clone`)
   - `MYSQL_USER`
   - `MYSQL_PASS`
   - `EUREKA_URL` (e.g., `http://localhost:8761/eureka` or the service URL if Railway exposes Eureka separately)

5. Use Railway's Dockerfile-based deployment: Railway will run `docker build` using `backend/Dockerfile` and start the container.

Notes and tips
- The `backend/Dockerfile` builds each module with Maven (skipping tests) and copies generated JARs into the runtime image.
- The container expects an external MySQL instance (do NOT run MySQL inside the same container in production). Use Railway MySQL plugin or PlanetScale.
- For compact free-tier usage, deploy the backend as one service (single container) so you avoid creating many separate Railway services.
- You will need to set production `GATEWAY_API_URL` in the frontend Vercel settings to point to the deployed gateway URL.

Commands (local verification)

Build image locally:
```bash
cd backend
docker build -t myproject-backend:latest .
docker run -e MYSQL_HOST=host -e MYSQL_PASS=pass -e MYSQL_USER=appuser -e MYSQL_DB=instagram_clone -p 8888:8888 myproject-backend:latest
```

Railway quick deploy (CLI)
```bash
# from repo root
railway init
railway link # link to project
railway up --service backend --dockerfile ./backend/Dockerfile
```

Fly.io quick deploy (CLI)
```bash
flyctl launch --name myproject-backend --no-deploy
# edit fly.toml to use the backend/Dockerfile or set build.args
flyctl deploy
```

What I'll do next (automatable tasks)
- Add a GitHub Actions workflow to build the Docker image and optionally push to a registry (requires secrets).
- Add a sample `railway.json` or `fly.toml` if you want me to (confirm target provider).

If you want me to continue, tell me to: "Proceed — deploy to Railway" or "Proceed — prepare Fly.io configs" and I will add the CI/CD workflow and provider-specific config files.
