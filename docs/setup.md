# Local Development Setup

## Prerequisites

| Tool | Version | Install |
|---|---|---|
| Node.js | 20+ | [nodejs.org](https://nodejs.org) |
| pnpm | 9+ | `npm install -g pnpm` |
| Docker | Latest | [docker.com](https://docker.com) |
| Docker Compose | v2+ | Included with Docker Desktop |

---

## Step 1: Clone & Install

```bash
git clone https://github.com/MLobbos/salonflow.git
cd salonflow
pnpm install
```

---

## Step 2: Start Local Services

```bash
docker compose -f infra/docker/docker-compose.yml up -d
```

This starts:
- PostgreSQL on port `5432` (user: `postgres`, password: `password`, db: `salonflow`)
- Redis on port `6379`

Check they're running:
```bash
docker compose -f infra/docker/docker-compose.yml ps
```

---

## Step 3: Configure Environment Variables

```bash
cp .env.example .env
```

Edit `.env`. For local development, the defaults for DATABASE_URL and REDIS_URL already match the Docker Compose setup. You need to fill in:

- `CLERK_SECRET_KEY` and `NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY` — get from [clerk.com](https://clerk.com) (free account)
- For WhatsApp testing, you can skip `META_*` vars until Phase 3

---

## Step 4: Run Database Migrations

```bash
pnpm --filter @salonflow/database db:migrate
```

This creates all tables in your local Postgres.

---

## Step 5: Start All Apps

```bash
pnpm dev
```

| App | URL |
|---|---|
| Web | http://localhost:3000 |
| API | http://localhost:3001 |
| Swagger | http://localhost:3001/docs |
| Prisma Studio | `pnpm --filter @salonflow/database db:studio` → http://localhost:5555 |

---

## Common Commands

```bash
# Run all tests
pnpm test

# Lint everything
pnpm lint

# Type check everything
pnpm typecheck

# Build all apps
pnpm build

# Reset local database (drops all data)
pnpm --filter @salonflow/database db:reset

# Open Prisma Studio (DB GUI)
pnpm --filter @salonflow/database db:studio

# Run only API in dev
pnpm --filter @salonflow/api dev

# Run only web in dev
pnpm --filter @salonflow/web dev
```

---

## Troubleshooting

**`pnpm install` fails**
- Make sure Node.js 20+ is installed: `node --version`
- Make sure pnpm 9+ is installed: `pnpm --version`

**`docker compose up` fails**
- Make sure Docker Desktop is running
- Check if ports 5432 or 6379 are already in use: `lsof -i :5432`

**`prisma migrate dev` fails**
- Make sure Postgres is running: `docker compose -f infra/docker/docker-compose.yml ps`
- Make sure `DATABASE_URL` in `.env` is correct

**Clerk auth not working**
- Verify `CLERK_SECRET_KEY` and `NEXT_PUBLIC_CLERK_PUBLISHABLE_KEY` are set in `.env`
- Check allowed origins in Clerk dashboard include `http://localhost:3000`
