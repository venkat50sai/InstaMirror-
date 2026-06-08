# Railway deployment: step-by-step (free tier)

This file lists exact steps to deploy the single-container `backend` to Railway and connect the frontend on Vercel.

Preconditions
- Push your repo to GitHub and ensure GitHub Actions workflow `build-backend-image.yml` exists (it does).
- Install Railway CLI locally (optional):
  ```bash
  npm install -g railway
  ```

Steps (Railway web UI - recommended for first run)
1. Create a Railway account (https://railway.app) and log in.
2. Create a new Project.
3. Add a MySQL plugin (Plugins -> Add Plugin -> MySQL). Note the connection info: host, user, password, database.
4. Add a new Service in the same Project and choose "Deploy from GitHub". Connect your GitHub repo and pick the branch.
   - Choose deployment method: Dockerfile (Railway will build using `backend/Dockerfile`).
5. In the Service settings -> Variables, add these environment variables (use the values from the MySQL plugin):
   - `MYSQL_HOST` → e.g. `containers-us-west-123.railway.app` (plugin host)
   - `MYSQL_PORT` → `3306`
   - `MYSQL_DB` → `instagram_clone` (or the DB from plugin)
   - `MYSQL_USER` → plugin user
   - `MYSQL_PASS` → plugin password
   - `EUREKA_URL` → `http://localhost:8761/eureka` (if Eureka runs inside same container you can leave default; otherwise set to the Eureka service URL that Railway exposes)

6. Trigger a deploy (Railway will build the Dockerfile and start the container). Monitor logs for errors.

Steps (Railway CLI - optional, for automation)
1. Login locally:
```bash
railway login
# or set token
export RAILWAY_TOKEN="<your_token>"
```
2. Link to the project (one-time):
```bash
railway link
# choose the existing project
```
3. Deploy the `backend` Dockerfile:
```bash
railway up --service backend --dockerfile ./backend/Dockerfile --detach
```

Set Vercel `GATEWAY_API_URL`
1. In Vercel dashboard, open the `myapp` project -> Settings -> Environment Variables.
2. Add `GATEWAY_API_URL` with the public URL of your Gateway service (e.g., `https://my-backend.up.railway.app` or the Railway service URL). Set it for the `Production` environment.
3. Redeploy the frontend from Vercel.

Troubleshooting
- If the container fails to start, check logs in Railway UI. Common issues:
  - Maven build errors: ensure JDK 21 and Maven are available (Dockerfile uses maven base).
  - Missing jars: ensure `mvn package` produced SNAPSHOT jars under each module's `target/` folder.
  - Database connection errors: verify plugin credentials and that `MYSQL_HOST` is reachable.

If you want, I can now add a GitHub Actions workflow that uses the Railway CLI to run `railway up` automatically when the backend image is published — you must provide a `RAILWAY_TOKEN` secret and the Project ID. Tell me to continue and I'll add that workflow and a small helper script.
