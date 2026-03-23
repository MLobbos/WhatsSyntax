# Roadmap

## Milestones

| Milestone | Goal | Status |
|---|---|---|
| v0.1 — Foundation | Monorepo, CI/CD, DB schema, API skeleton | In Progress |
| v0.2 — Booking Engine | Full booking CRUD + availability | Planned |
| v0.3 — WhatsApp Bot | End-to-end booking via WhatsApp | Planned |
| v0.4 — Web Dashboard | Usable admin dashboard | Planned |
| v0.5 — Reminders | WhatsApp notifications live | Planned |
| v1.0 — Launch | Onboarding, billing, first paying salon | Planned |

---

## Phase 0: Foundation (~1 week)

**Goal**: Clean repo, local dev running, DB schema defined, API boots.

Deliverables:
- Turborepo monorepo with `apps/api`, `apps/web`, all packages
- Docker Compose (Postgres + Redis)
- Prisma schema v1
- NestJS boots with health check
- Next.js boots with basic layout
- GitHub Actions CI (lint + typecheck + test)
- All GitHub files (README, AGENTS.md, docs/)

---

## Phase 1: Core Backend (~1.5 weeks)

**Goal**: Auth working, business/service/staff CRUD APIs complete.

Deliverables:
- Clerk auth integrated
- `POST /businesses`, `GET /businesses/:id`
- `POST /services`, `GET /services`
- `POST /staff`, `GET /staff`
- `POST /availability-windows` (weekly schedule)
- Prisma migrations
- Unit tests

Dependencies: Phase 0

---

## Phase 2: Booking Engine (~1.5 weeks)

**Goal**: Core availability + booking logic working and tested.

Deliverables:
- `GET /availability?businessId=&serviceId=&date=`
- `POST /bookings`
- `PATCH /bookings/:id` (cancel/reschedule)
- Double-booking conflict detection
- Unit tests for edge cases

Dependencies: Phase 1

---

## Phase 3: WhatsApp Bot (~2 weeks)

**Goal**: Customer can book via WhatsApp.

Deliverables:
- Meta Cloud API webhook registered
- Conversation FSM: SELECT_SERVICE → SELECT_STAFF → SELECT_DATE → SELECT_TIME → CONFIRM → BOOKED
- Redis-backed session state
- Confirmation message sent
- Meta template messages approved

Dependencies: Phase 2

**Risk**: Meta template approval can take 1–5 days. Apply early.

---

## Phase 4: Customer Booking Page (~1 week)

**Goal**: Web alternative to WhatsApp booking.

Deliverables:
- `/book/:slug` public page
- Service + staff selector
- Date + time slot picker
- Contact form
- Confirmation screen + .ics download

Dependencies: Phase 2

---

## Phase 5: Admin Dashboard (~2 weeks)

**Goal**: Salon owners can manage everything from the web.

Deliverables:
- Calendar view (day/week)
- Bookings list + detail + manage
- Services CRUD
- Staff CRUD + schedule
- Basic analytics (bookings count, upcoming today)

Dependencies: Phases 1–2

---

## Phase 6: Notifications & Reminders (~1 week)

**Goal**: Automated WhatsApp messages.

Deliverables:
- Booking confirmation → WhatsApp to customer
- Reminder 24h before
- Reminder 2h before
- Cancellation notification
- BullMQ scheduler

Dependencies: Phases 2–3

---

## Phase 7: Onboarding & Billing (~2 weeks)

**Goal**: Self-serve signup → first booking in < 10 minutes.

Deliverables:
- Onboarding wizard (connect WA number, add services, set schedule)
- Stripe Free/Pro plans
- Feature enforcement (booking limits, staff limits)
- Billing portal

Dependencies: All previous phases
