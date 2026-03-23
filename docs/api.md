# API Reference

Base URL: `http://localhost:3001` (dev) | `https://api.salonflow.app` (prod)

Swagger UI: `{API_URL}/docs`

All authenticated endpoints require:
```
Authorization: Bearer <clerk-jwt-token>
```

---

## Health

### `GET /health`
Public. Returns API status.

**Response** `200`:
```json
{
  "status": "ok",
  "timestamp": "2024-05-01T10:00:00.000Z",
  "service": "salonflow-api"
}
```

---

## Businesses

### `POST /businesses`
Create a new business. Auth required. Creates business linked to the Clerk user.

**Body**:
```json
{
  "name": "Salon Belle",
  "slug": "salon-belle",
  "phone": "+4917612345678",
  "email": "info@salonbelle.de",
  "timezone": "Europe/Berlin"
}
```

**Response** `201`: BusinessDto

---

### `GET /businesses/:id`
Get business by ID. Auth required. Must be owner.

**Response** `200`: BusinessDto

---

## Services

### `POST /businesses/:businessId/services`
Create a service. Auth required.

**Body**:
```json
{
  "name": "Haircut",
  "description": "Classic haircut and styling",
  "durationMins": 45,
  "priceCents": 3500
}
```

**Response** `201`: ServiceDto

---

### `GET /businesses/:businessId/services`
List all active services. **Public** (used by booking page).

**Response** `200`: `ServiceDto[]`

---

## Staff

### `POST /businesses/:businessId/staff`
Add staff member. Auth required.

**Body**:
```json
{
  "name": "Maria",
  "email": "maria@salonbelle.de"
}
```

**Response** `201`: StaffDto

---

### `GET /businesses/:businessId/staff`
List active staff. **Public** (used by booking page).

**Response** `200`: `StaffDto[]`

---

## Availability

### `GET /availability`
Get available time slots. **Public**.

**Query params**:
- `businessId` (required)
- `serviceId` (required)
- `date` — `YYYY-MM-DD` in business timezone (required)
- `staffId` — filter by staff (optional)

**Response** `200`:
```json
[
  {
    "startAt": "2024-05-10T08:00:00.000Z",
    "endAt": "2024-05-10T08:45:00.000Z",
    "staffId": "clx...",
    "available": true
  }
]
```

---

## Bookings

### `POST /bookings`
Create a booking. **Public** (customers book via web page or WhatsApp bot).

**Body**:
```json
{
  "serviceId": "clx...",
  "staffId": "clx...",
  "startAt": "2024-05-10T08:00:00.000Z",
  "customerName": "Mia Müller",
  "customerPhone": "+4917687654321",
  "channel": "WEB"
}
```

**Response** `201`: BookingDto

**Error** `409`: Time slot no longer available

---

### `GET /bookings`
List bookings for the authenticated business. Auth required.

**Query params**:
- `date` — filter by date (`YYYY-MM-DD`)
- `staffId` — filter by staff
- `status` — filter by status

**Response** `200`: `BookingDto[]`

---

### `PATCH /bookings/:id`
Update booking status. Auth required.

**Body**:
```json
{
  "status": "CANCELLED"
}
```

**Response** `200`: BookingDto

---

## WhatsApp Webhook

### `GET /whatsapp/webhook`
Meta verification challenge. Returns `hub.challenge`.

### `POST /whatsapp/webhook`
Receives incoming WhatsApp messages. Always returns `200` immediately.
Processing is async via BullMQ.
