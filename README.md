# Fix Set Go

Full-stack business website starter for Fix Set Go.

## What is included

- React + Vite frontend with responsive business landing page
- Spring Boot backend with REST APIs
- H2 database for local development
- User registration, login, BCrypt password hashing, and signed token authentication
- Public contact lead form saved into the database

## Run backend

```powershell
cd backend
mvn spring-boot:run
```

Backend runs at `http://localhost:8081`.

## Run frontend

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Frontend runs at `http://localhost:5173`.

## Useful API routes

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `POST /api/leads`
- `GET /api/leads` with `Authorization: Bearer <token>`

## Database

Local H2 database files are created in `backend/data`. The H2 console is enabled at `http://localhost:8081/h2-console`.

## Deploy to Railway

This repo is a monorepo. Create two Railway services from the same GitHub repo:

1. `backend` service (Root Directory: `backend`)
2. `frontend` service (Root Directory: `frontend`)

### 1) Push code to GitHub

```powershell
git init
git add .
git commit -m "Prepare Railway deployment"
git branch -M main
git remote add origin <YOUR_GITHUB_REPO_URL>
git push -u origin main
```

### 2) Railway backend service settings

- Root Directory: `backend`
- Build Command: `mvn -DskipTests clean package`
- Start Command: `java -jar target/fix-set-go-backend-0.0.1-SNAPSHOT.jar`

Set environment variables:

- `FIX_SET_GO_JWT_SECRET` (required in production)
- `APP_CORS_ALLOWED_ORIGIN_PATTERNS` (set to frontend Railway domain, e.g. `https://your-frontend.up.railway.app`)
- `FIX_SET_GO_SMTP_USERNAME` and `FIX_SET_GO_SMTP_PASSWORD` (optional, for email sending)

### 3) Railway frontend service settings

- Root Directory: `frontend`
- Build Command: `npm ci && npm run build`
- Start Command: `npm run start`

Set environment variable:

- `VITE_API_URL` = backend Railway URL, e.g. `https://your-backend.up.railway.app`

### 4) Add Google Ads after frontend domain is live

Add AdSense script in `frontend/index.html`:

```html
<script async src="https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js?client=ca-pub-XXXXXXXXXXXXXXXX" crossorigin="anonymous"></script>
```
