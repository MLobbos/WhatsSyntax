# Architecture

## System Overview

SalonFlow is a WhatsApp-first booking SaaS for hair salons and beauty studios.

```
WhatsApp Client
      │
      ▼
Meta Cloud API  ←──  (webhook POST to /whatsapp/webhook)
      │
      ▼
┌─────────────────────────────────────────┐
│           API Server (NestJS)            │
│  ┌────────────────┐  ┌────────────────┐ │
│  │   REST API     │  │  WhatsApp Bot  │ │
│  │  /businesses   │  │  Webhook +     │ │
│  │  /services     │  │  FSM Logic     │ │
│  │  /staff        │  │  (Redis state) │ │
│  │  /bookings     │  └────────────────┘ │
│  │  /availability │                     │
│  └────────────────┘                     │
└───────────────┬─────────────────────────┘
                │
     ┌──────────┴──────────┐
     ▼                     ▼
PostgreSQL               Redis
(Supabase)               ├── Bot sessions (bot:session:{phone})
                         └── BullMQ job queue (reminders)
                │
     ┌──────────┴──────────┐
     ▼
Next.js (Vercel)
├── /                     → Landing page
├── /dashboard            → Admin dashboard (protected by Clerk)
│   ├── /calendar         → Appointment calendar
│   ├── /bookings         → Booking list + management
│   ├── /services         → Services CRUD
│   ├── /staff            → Staff CRUD
│   └── /settings         → Business settings
└── /book/:slug           → Public customer booking page (no auth)
```

---

## Services

### `apps/api` — NestJS Backend

Modules:
- `HealthModule` — `GET /health`
- `BusinessModule` — Business CRUD (Phase 1)
- `StaffModule` — Staff CRUD + availability (Phase 1)
- `ServiceModule` — Service CRUD (Phase 1)
- `BookingModule` — Booking + availability logic (Phase 2)
- `WhatsAppModule` — Meta webhook handler + FSM (Phase 3)
- `NotificationModule` — BullMQ reminders (Phase 6)

### `apps/web` — Next.js Frontend

Route groups:
- `(public)/` — Landing page, `/book/:slug`
- `(dashboard)/` — All protected admin routes
- `(auth)/` — Clerk sign-in, sign-up

### `packages/database` — Prisma

Single source of truth for the DB schema. Shared by `apps/api`.
Generated client lives in `packages/database/generated/`.

---

## Database Schema (ERD)

```
Business ──┬── Staff ──── Availability
           ├── Service ── StaffService (Staff ↔ Service M2M)
           ├── Customer
           └── Booking (Customer + Service + Staff)
```

Key design decisions:
- `businessId` on every entity — all queries must be scoped to it
- All times stored in UTC (`startAt`, `endAt` on Booking)
- Business has a `timezone` field for local time display
- Customer identified by `(businessId, phone)` — unique per business
- Booking `channel` tracks whether it came via WhatsApp, web, or manual entry

---

## WhatsApp Bot Flow (FSM)

```
Customer sends message to salon's WhatsApp number
      │
      ▼
WELCOME ──→ SELECT_SERVICE ──→ SELECT_STAFF ──→ SELECT_DATE ──→ SELECT_TIME ──→ CONFIRM ──→ BOOKED
                                                                                    │
                                                                               CANCELLED (user says "cancel")
```

State stored in Redis: `bot:session:{phoneNumber}` (TTL: 30 minutes)

---

## Auth (Clerk)

- Salon owners authenticate via Clerk
- JWT token sent as `Authorization: Bearer <token>` header
- `businessId` extracted from JWT claims in all protected controllers
- Public routes: `GET /health`, `POST /whatsapp/webhook`, `GET /availability`, `POST /bookings` (via public booking page)

---

## Background Jobs (BullMQ + Redis)

Queues:
- `reminders` — 24h and 2h before appointment (WhatsApp template messages)
- `notifications` — Booking confirmation on creation

---

## Hosting

| Service | Hosting | Notes |
|---|---|---|
| `apps/api` | Railway | Auto-deploy on push to `main` |
| `apps/web` | Vercel | Native Next.js, free tier |
| PostgreSQL | Supabase | Managed, free tier, dashboard |
| Redis | Railway add-on | Managed Redis |

---

## Environment Variables

See `.env.example` for all required variables.
