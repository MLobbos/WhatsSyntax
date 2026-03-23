# AGENTS.md — AI Agent Guide for SalonFlow

This file instructs AI coding agents (Claude, Copilot, Cursor, etc.) on how to work in this repository.

---

## Project Summary

**SalonFlow** is a WhatsApp-first booking SaaS for hair salons and beauty studios.

Salon owners connect their WhatsApp Business number. Clients book via WhatsApp conversation — no app download needed.

**Tech stack:**
- Backend: NestJS + Fastify, Prisma, PostgreSQL, Redis, BullMQ
- Frontend: Next.js 14 (App Router), Tailwind CSS, Clerk auth
- WhatsApp: Meta Cloud API
- Monorepo: Turborepo + pnpm

---

## Monorepo Layout

```
apps/
  api/                  NestJS backend
    src/
      main.ts           Entry point
      app.module.ts     Root module
      health/           Health check (Phase 0, done)
      business/         Business CRUD (Phase 1)
      staff/            Staff CRUD + availability (Phase 1)
      service/          Service CRUD (Phase 1)
      booking/          Booking + availability logic (Phase 2)
      whatsapp/         Meta webhook + FSM (Phase 3)
      notification/     BullMQ reminders (Phase 6)
  web/                  Next.js frontend
    src/app/
      (public)/         Landing page, /book/:slug
      (dashboard)/      Protected admin routes
      (auth)/           Clerk sign-in, sign-up

packages/
  database/             Prisma schema + generated client
    prisma/schema.prisma
    index.ts            Re-exports PrismaClient and all types
  types/                Shared TypeScript DTOs
    index.ts
  ui/                   Shared React components
    src/button.tsx
  config/               Shared ESLint, TSConfig, Prettier

infra/
  docker/
    docker-compose.yml  Local Postgres + Redis
    Dockerfile.api      Production API image
  scripts/
    setup-dev.sh        One-command local dev setup

docs/
  architecture.md       System design and data flow
  product.md            Vision, personas, features
  roadmap.md            Phases and milestones
  api.md                REST API reference
  setup.md              Local dev setup guide
```

---

## Current Phase

**Phase 0 — Foundation** (complete)

Next: **Phase 1 — Core Backend**
- Add `BusinessModule` to `apps/api/src/`
- Add `StaffModule` to `apps/api/src/`
- Add `ServiceModule` to `apps/api/src/`
- Integrate Clerk JWT guard
- Write Prisma migrations for all three

---

## Key Conventions

- **TypeScript strict mode** — no `any`
- **All DB queries scoped by `businessId`** — every entity belongs to a business
- **Phone numbers in E.164 format** — `+491234567890`
- **All timestamps in UTC** — stored as `DateTime` in Prisma
- **Conventional Commits** — `feat(api): ...`, `fix(bot): ...`
- **One Prisma migration per schema change** — never edit generated files

---

## Adding a New Module (Phase 1+)

1. Create `apps/api/src/<module>/` with:
   - `<module>.module.ts`
   - `<module>.controller.ts`
   - `<module>.service.ts`
   - `<module>.dto.ts`
2. Import the module in `app.module.ts`
3. Add types to `packages/types/index.ts`
4. If schema changes: run `pnpm --filter @salonflow/database db:migrate`

---

## Do Not

- Commit `.env` files
- Edit `packages/database/generated/` — always regenerate via `db:generate`
- Add Android/Kotlin/Gradle files — this is a Node.js/TypeScript-only repo
- Add WhatsSyntax-related code — this is SalonFlow only
