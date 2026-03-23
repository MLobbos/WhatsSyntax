# Product

## Vision

SalonFlow automates appointment bookings for hair salons and beauty studios via WhatsApp — the messaging app their clients already use every day.

No new app for clients to download. No login required. Just send a WhatsApp message and book in 60 seconds.

---

## Problem

Hair salons and beauty studios handle bookings manually:
- Clients send WhatsApp messages: "Can I book for Friday at 3pm?"
- Owner replies manually (sometimes hours later)
- Double-bookings happen
- No reminders → no-shows are common
- Owner is stuck on their phone instead of doing their job

---

## Solution

SalonFlow connects to the salon's WhatsApp Business number. When a client messages to book, a bot handles the entire conversation automatically:

1. Client sends any message (or "book")
2. Bot shows available services
3. Client selects service
4. Bot shows available staff
5. Client selects staff (or "any")
6. Bot shows available dates
7. Client picks a date
8. Bot shows available time slots
9. Client picks a time
10. Bot confirms and creates booking
11. Client gets a confirmation + reminder before the appointment

The salon owner sees everything in the web dashboard.

---

## User Personas

### Persona 1: The Salon Owner (Primary User)
- Name: Sarah, 34
- Runs a 3-person hair salon
- Currently manages bookings via WhatsApp manually
- Loses 2–3 bookings per week because she can't reply fast enough
- Not tech-savvy — needs an easy setup
- Wants: automated booking, fewer no-shows, a clear calendar view

### Persona 2: The Client (Bot User)
- Name: Mia, 27
- Books her haircut every 6 weeks
- Already uses WhatsApp daily
- Doesn't want to download a new app
- Wants: fast booking, confirmation message, reminder before appointment

---

## Features

### Phase 0 — Foundation
- [ ] Monorepo setup, CI/CD, DB schema, API skeleton

### Phase 1 — Core Backend
- [ ] Salon onboarding (create business, services, staff)
- [ ] Auth (Clerk)
- [ ] Business/Service/Staff CRUD APIs

### Phase 2 — Booking Engine
- [ ] Availability calculation
- [ ] Booking creation with conflict prevention
- [ ] Booking management (reschedule, cancel)

### Phase 3 — WhatsApp Bot
- [ ] Meta Cloud API integration
- [ ] Full booking conversation flow
- [ ] Confirmation message on booking

### Phase 4 — Customer Booking Page
- [ ] Public `/book/:slug` page
- [ ] Service selection UI
- [ ] Date + time picker
- [ ] Booking form

### Phase 5 — Admin Dashboard
- [ ] Calendar view (day/week)
- [ ] Booking detail + management
- [ ] Services and staff management
- [ ] Basic analytics

### Phase 6 — Reminders
- [ ] WhatsApp reminder 24h before
- [ ] WhatsApp reminder 2h before
- [ ] Cancellation notification

### Phase 7 — Billing
- [ ] Free/Pro plans
- [ ] Stripe integration
- [ ] Self-serve onboarding wizard

---

## Success Metrics

- Bookings per week per salon (target: 20+ for Pro retention)
- No-show rate reduction (target: -30% vs manual)
- Time to first booking via WhatsApp (target: <3 minutes)
- Salon activation rate (target: first booking within 48h of signup)
