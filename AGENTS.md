# AGENTS.md — AI Agent Guide for WhatsSyntax

This file instructs AI coding agents (Claude, Copilot, Cursor, etc.) on how to work in this repository.

---

## Project Summary

**WhatsSyntax** is a WhatsApp-clone Android app built with Kotlin. It replicates the core WhatsApp experience: real-time 1:1 messaging, contacts, status updates, and call history.

**Tech stack:**
- Android: Kotlin, MVVM + Repository pattern, Hilt DI, Room (local), Retrofit + OkHttp (remote), Coil (images)
- Backend: Ktor (Kotlin), Exposed ORM, PostgreSQL 15, Redis 7, JWT auth
- Shared: `packages/shared-models` — pure Kotlin/JVM DTOs used by both Android and backend

---

## Monorepo Layout

```
app/                    Android application (Kotlin, MVVM, Hilt)
  src/main/java/.../
    MainActivity.kt
    WhatsSyntaxApplication.kt
    adapter/            RecyclerView adapters
    data/
      Datasource.kt     DEPRECATED — do not use from Fragments or ViewModels
      model/            Domain models (being migrated to use String IDs + URL images)
      local/            Room DAOs and database (Phase 2)
      remote/           Retrofit ApiService and WebSocket client (Phase 1+)
    di/                 Hilt modules (AppModule, NetworkModule, DatabaseModule)
    ui/                 Fragments
    viewmodel/          ViewModels (Phase 1+)
    repository/         Repositories bridging local/remote (Phase 2+)

packages/shared-models/ Pure Kotlin/JVM DTOs — no Android imports allowed
  src/main/kotlin/.../
    AuthDto.kt
    UserDto.kt
    ChatDto.kt
    MessageDto.kt
    StatusDto.kt
    CallDto.kt

services/api/           Ktor backend
  src/main/kotlin/.../
    Application.kt      Entry point
    plugins/            Ktor plugin configuration
    routing/            Route handlers per domain
    data/               Exposed DAOs and table definitions (Phase 1+)
    websocket/          WebSocket session manager (Phase 3+)

infra/
  docker-compose.yml    Local Postgres + Redis + API
  scripts/
    setup-dev.sh        One-command local setup
    init-db.sql         DB initialization

docs/                   Architecture, API, data model, setup
.github/workflows/      CI: android-ci.yml, backend-ci.yml
```

---

## Architecture Rules

### Android

- **Fragments must NOT instantiate `Datasource` directly.** `Datasource.kt` is being retired. All data access goes through a `ViewModel` → `Repository` chain.
- **ViewModels must NOT import Android UI classes** (`View`, `Fragment`, `Activity`). Use `StateFlow` or `LiveData` to communicate with the UI layer.
- **All async work uses Kotlin Coroutines** with `viewModelScope` or `lifecycleScope`. No `AsyncTask`, no `Thread`.
- **Hilt DI is mandatory** for all `ViewModel` and `Repository` instances. Do not instantiate these with `by viewModels { MyViewModel() }` without Hilt.
- **Images are loaded with Coil** — never call `setImageResource(R.drawable.img_*)` for user content. Use `imageView.load(url) { placeholder(...) }`.
- **Contact.image (Int)** is deprecated — use `avatarUrl: String?` instead. Every PR that adds a new contact image reference must use the URL field.

### Backend

- **All routes under `/v1/auth/**` are public.** Every other route requires JWT bearer auth.
- **JWT user ID** is always extracted from the token, never from the request body or query params.
- **Database queries use Exposed DSL**, not raw SQL. Use transactions for multi-step writes.
- **OTP dev bypass:** set `DEV_OTP_BYPASS=123456` to skip SMS and accept that code in tests/dev. Never enable in production.
- **No business logic in routing handlers** — routing calls a function from `domain/`. Route files should be thin.

### Shared Models

- `packages/shared-models` must remain a **pure Kotlin/JVM library** — no Android SDK imports.
- All DTOs are annotated with `@Serializable` (kotlinx.serialization).
- Any change to a DTO field name or type is a **breaking change** — coordinate Android + backend changes in one PR.

---

## What NOT To Do

- **Do NOT commit `.env` files** — use `.env.example` for documentation only.
- **Do NOT call `Datasource()` from a Fragment or ViewModel** — this is the pattern we're removing.
- **Do NOT add `android.*` imports to `packages/shared-models`** — it must stay pure JVM.
- **Do NOT use `setImageResource()` for contact/user avatars** — use Coil with URL.
- **Do NOT store JWT tokens in `SharedPreferences`** — use `DataStore` (encrypted).
- **Do NOT skip the Hilt annotation processor** — always add `kapt("hilt-android-compiler")` for any module using Hilt.
- **Do NOT use `notifyDataSetChanged()`** — prefer `DiffUtil.ItemCallback` in all adapters.
- **Do NOT use `Handler` or `postDelayed` for coroutine work** — use `delay()` in a coroutine.

---

## Commit Convention (Conventional Commits)

```
feat(android): add ChatRepository with Room DAO
fix(backend): correct JWT expiry validation
chore(ci): add lint step to GitHub Actions workflow
docs(api): document /v1/messages POST endpoint
refactor(android): migrate ChatFragment to ChatViewModel
test(shared): add serialization roundtrip tests for MessageDto
```

Format: `type(scope): description`
Scopes: `android`, `backend`, `shared`, `infra`, `ci`, `docs`

---

## Branch Naming

```
feature/issue-N-short-description
fix/issue-N-short-description
chore/short-description
docs/short-description
```

---

## Running the Project

```bash
# Prerequisites: Java 17+, Docker, Android Studio

# One-command dev setup (starts Postgres + Redis, builds shared-models)
./infra/scripts/setup-dev.sh

# Start backend only
./gradlew :services:api:run

# Run all tests
./gradlew test

# Run Android lint
./gradlew :app:lintDebug

# Build debug APK
./gradlew :app:assembleDebug
```

---

## Key Files to Know

| File | Purpose |
|---|---|
| `app/src/main/java/.../data/Datasource.kt` | **DEPRECATED** — being replaced by Room + API; do not add new calls to this |
| `app/src/main/java/.../data/model/Contact.kt` | Core domain model — `image: Int` is being migrated to `avatarUrl: String?` |
| `app/src/main/java/.../data/model/Message.kt` | Core message model — needs `id`, `chatId`, `senderId` fields for Phase 2 |
| `app/src/main/java/.../ui/ChatDetailFragment.kt` | First Fragment to migrate to ViewModel in Phase 2 |
| `packages/shared-models/src/.../MessageDto.kt` | Canonical message DTO for Android ↔ backend |
| `services/api/src/.../routing/AuthRoutes.kt` | Auth endpoints stub — wire real JWT + Postgres in Phase 1 |
| `services/api/src/main/resources/application.conf` | Backend config — all env vars documented here |
| `infra/docker-compose.yml` | Local dev: Postgres + Redis + API |
| `docs/architecture.md` | System design overview |
| `docs/api.md` | REST endpoint catalogue |

---

## Environment Variables

See `.env.example` for all required variables with descriptions.

### Key variables

| Variable | Description | Default (dev) |
|---|---|---|
| `DATABASE_URL` | Postgres JDBC URL | `jdbc:postgresql://localhost:5432/whatssyntax` |
| `REDIS_URL` | Redis URL | `redis://localhost:6379` |
| `JWT_SECRET` | HMAC-256 secret for JWT signing | dev-only placeholder |
| `DEV_OTP_BYPASS` | Fixed OTP code that skips SMS (dev/test only) | `123456` |
| `PORT` | Backend HTTP port | `8080` |
