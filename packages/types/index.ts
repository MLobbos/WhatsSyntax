// ─── API Response Wrappers ────────────────────────────────────────────────────

export interface ApiResponse<T> {
  data: T;
  message?: string;
}

export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  pageSize: number;
}

// ─── Business ─────────────────────────────────────────────────────────────────

export interface BusinessDto {
  id: string;
  name: string;
  slug: string;
  phone: string;
  email: string | null;
  address: string | null;
  timezone: string;
  active: boolean;
}

export interface CreateBusinessDto {
  name: string;
  slug: string;
  phone: string;
  email?: string;
  address?: string;
  timezone?: string;
}

// ─── Service ──────────────────────────────────────────────────────────────────

export interface ServiceDto {
  id: string;
  businessId: string;
  name: string;
  description: string | null;
  durationMins: number;
  priceCents: number | null;
  active: boolean;
}

export interface CreateServiceDto {
  name: string;
  description?: string;
  durationMins: number;
  priceCents?: number;
}

// ─── Staff ────────────────────────────────────────────────────────────────────

export interface StaffDto {
  id: string;
  businessId: string;
  name: string;
  email: string | null;
  active: boolean;
}

export interface CreateStaffDto {
  name: string;
  email?: string;
  phone?: string;
}

// ─── Booking ──────────────────────────────────────────────────────────────────

export type BookingStatus = 'PENDING' | 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'NO_SHOW';
export type BookingChannel = 'WHATSAPP' | 'WEB' | 'MANUAL';

export interface BookingDto {
  id: string;
  businessId: string;
  customerId: string;
  serviceId: string;
  staffId: string;
  status: BookingStatus;
  channel: BookingChannel;
  startAt: string; // ISO 8601 UTC
  endAt: string;   // ISO 8601 UTC
  notes: string | null;
}

export interface CreateBookingDto {
  serviceId: string;
  staffId: string;
  startAt: string; // ISO 8601 UTC
  customerName: string;
  customerPhone: string; // E.164
  customerEmail?: string;
  notes?: string;
  channel?: BookingChannel;
}

// ─── Availability ─────────────────────────────────────────────────────────────

export interface TimeSlot {
  startAt: string; // ISO 8601 UTC
  endAt: string;   // ISO 8601 UTC
  staffId: string;
  available: boolean;
}

export interface AvailabilityQuery {
  businessId: string;
  serviceId: string;
  staffId?: string;
  date: string; // YYYY-MM-DD in business timezone
}

// ─── WhatsApp Bot ─────────────────────────────────────────────────────────────

export type BotStep =
  | 'WELCOME'
  | 'SELECT_SERVICE'
  | 'SELECT_STAFF'
  | 'SELECT_DATE'
  | 'SELECT_TIME'
  | 'CONFIRM'
  | 'BOOKED'
  | 'CANCELLED';

export interface BotSession {
  step: BotStep;
  businessId: string;
  serviceId?: string;
  staffId?: string;
  date?: string;       // YYYY-MM-DD
  startAt?: string;    // ISO 8601 UTC
  customerName?: string;
  expiresAt: number;   // Unix timestamp
}
