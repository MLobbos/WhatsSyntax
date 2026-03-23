# AGENTS.md — Guide for AI Agents

This file instructs AI coding agents (Claude, Copilot, Cursor, etc.) on how to work in this repository.

---

## Project Summary

**SalonFlow** is a WhatsApp-first booking SaaS for hair salons and beauty studios.

- Clients book appointments by messaging the salon's WhatsApp number
- The WhatsApp bot guides the conversation (service → staff → date → time → confirm)
- Salon owners manage everything from a web admin dashboard
- Automated WhatsApp reminders are sent before appointments

---

## Monorepo Layout

```
apps/api/          NestJS backend (REST API + WhatsApp bot webhook handler)
apps/web/          Next.js 14 frontend (admin dashboard + /book/:slug public page)
packages/database/ Prisma schema and generated client — single source of truth for DB
packages/types/    Shared TypeScript interfaces (no runtime code)
packages/ui/       Shared shadcn/ui React components
packages/config/   Shared ESLint, TSConfig, Prettier configs
docs/              All documentation — keep in sync with code changes
infra/docker/      Local dev Docker Compose (Postgres + Redis)
.github/           CI/CD workflows and PR/issue templates
```

---

## Key Conventions

### TypeScript
- Strict mode enabled everywhere (`"strict": true`)
- No `any` — use `unknown` and narrow types
- Export types from `packages/types`, not from `apps/`

### NestJS (apps/api)
- One module per domain: `BusinessModule`, `BookingModule`, `StaffModule`, etc.
- All database queries use the Prisma client from `@salonflow/database`
- All protected routes use `@UseGuards(ClerkAuthGuard)`
- Every controller must extract `businessId` from the JWT — never trust client-sent businessId
- Return HTTP exceptions with `HttpException` or NestJS built-ins

### Next.js (apps/web)
- App Router only — no Pages Router
- Server Components by default; use `"use client"` only when necessary
- Protected routes go under `app/(dashboard)/`
- Public booking pages go under `app/(public)/book/[slug]/`
- API calls from server components use fetch with revalidation; from client use SWR or React Query

### Database / Prisma
- All schema changes go through migrations (`prisma migrate dev`)
- Never edit the generated client — it lives in `packages/database/generated/`
- All timestamps stored in UTC
- Multi-tenant: every query must be scoped by `businessId`

### WhatsApp Bot
- Bot logic lives in `apps/api/src/whatsapp/`
- Conversation state is stored in Redis with key `bot:session:{phoneNumber}`
- All incoming webhook requests return `200 OK` immediately; processing is async
- Template messages must be pre-approved by Meta before use in production

### Commit Convention (Conventional Commits)
```
feat(api): add endpoint for booking creation
fix(bot): handle null service selection in FSM
chore(infra): update docker-compose postgres version
docs(api): add OpenAPI annotations to availability endpoint
test(booking): add unit tests for conflict detection
```

### Branch Naming
```
feat/issue-number-short-description
fix/issue-number-short-description
chore/short-description
docs/short-description
```

---

## What NOT to Do

- Do NOT commit `.env` files — use `.env.example` for documentation
- Do NOT bypass Clerk auth on any route that touches business data
- Do NOT store raw phone numbers without normalizing to E.164 format (`+491234567890`)
- Do NOT call Prisma directly from `apps/web` — go through the API
- Do NOT add new packages to `packages/` without updating `pnpm-workspace.yaml`
- Do NOT use `any` type — use proper types or `unknown`
- Do NOT hardcode business IDs or phone numbers anywhere

---

## Running the Project

```bash
# Install all dependencies
pnpm install

# Start local services
docker compose -f infra/docker/docker-compose.yml up -d

# Run migrations
pnpm --filter @salonflow/database db:migrate

# Start all apps
pnpm dev

# Run tests
pnpm test

# Lint + typecheck
pnpm lint
pnpm typecheck
```

---

## Key Files to Know

| File | Purpose |
|---|---|
| `packages/database/prisma/schema.prisma` | The DB schema — read this before any data work |
| `apps/api/src/app.module.ts` | Root NestJS module — all modules registered here |
| `apps/web/app/layout.tsx` | Root Next.js layout with Clerk provider |
| `apps/api/src/whatsapp/whatsapp.service.ts` | WhatsApp bot logic |
| `apps/api/src/booking/booking.service.ts` | Core booking + availability logic |
| `docs/architecture.md` | Full system design |

---

## Environment Variables

See `.env.example` for all required variables with descriptions.
