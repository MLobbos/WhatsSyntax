# SalonFlow

> WhatsApp-first booking automation for hair salons and beauty studios.

Salon owners connect their WhatsApp Business number. Clients book appointments directly via WhatsApp conversation — no app download, no manual back-and-forth.

[![CI](https://github.com/MLobbos/salonflow/actions/workflows/ci.yml/badge.svg)](https://github.com/MLobbos/salonflow/actions/workflows/ci.yml)

---

## What It Does

- **WhatsApp Bot** — Clients message the salon's WhatsApp number. The bot guides them through service → staff → date → time → confirm.
- **Admin Dashboard** — Salon owners manage services, staff, availability, and appointments from a web dashboard.
- **Public Booking Page** — `/book/:slug` web page as an alternative booking channel.
- **Automated Reminders** — WhatsApp messages 24h and 2h before each appointment.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend API | NestJS + Fastify |
| Frontend | Next.js 14 (App Router) |
| Database | PostgreSQL (Supabase) |
| ORM | Prisma |
| WhatsApp | Meta Cloud API |
| Auth | Clerk |
| Background Jobs | BullMQ + Redis |
| Monorepo | Turborepo + pnpm |
| Hosting (API) | Railway |
| Hosting (Web) | Vercel |

---

## Monorepo Structure

```
salonflow/
├── apps/
│   ├── api/          # NestJS backend + WhatsApp bot
│   └── web/          # Next.js admin dashboard + public booking page
├── packages/
│   ├── database/     # Prisma schema + generated client
│   ├── types/        # Shared TypeScript types
│   ├── ui/           # Shared React components
│   └── config/       # Shared ESLint, TSConfig, Prettier
├── docs/             # Architecture, API, product, setup docs
└── infra/            # Docker Compose, Dockerfile
```

---

## Quick Start

### Prerequisites

- Node.js 20+
- pnpm 9+
- Docker + Docker Compose

### Setup

```bash
# Clone the repo
git clone https://github.com/MLobbos/salonflow.git
cd salonflow

# Install dependencies
pnpm install

# Start local services (Postgres + Redis)
docker compose -f infra/docker/docker-compose.yml up -d

# Copy and fill in environment variables
cp .env.example .env

# Run database migrations
pnpm --filter @salonflow/database db:migrate

# Start all apps in development mode
pnpm dev
```

API: `http://localhost:3001` | Web: `http://localhost:3000` | Swagger: `http://localhost:3001/docs`

---

## Documentation

| Doc | Description |
|---|---|
| [Architecture](docs/architecture.md) | System design, data flow, DB schema |
| [Product](docs/product.md) | Vision, personas, feature list |
| [Roadmap](docs/roadmap.md) | Phases and milestones |
| [API Reference](docs/api.md) | REST API endpoints |
| [Setup Guide](docs/setup.md) | Detailed local dev setup |
| [Contributing](CONTRIBUTING.md) | Branching, commits, PR process |

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

---

## License

MIT — see [LICENSE](LICENSE).
