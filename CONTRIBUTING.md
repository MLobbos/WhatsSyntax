# Contributing to SalonFlow

Thank you for contributing. Please read this guide before opening a PR.

---

## Prerequisites

- Node.js 20+
- pnpm 9+
- Docker + Docker Compose

---

## Local Setup

```bash
git clone https://github.com/MLobbos/salonflow.git
cd salonflow
pnpm install
docker compose -f infra/docker/docker-compose.yml up -d
cp .env.example .env
# Fill in .env with your local values
pnpm --filter @salonflow/database db:migrate
pnpm dev
```

---

## Branch Strategy

We use **GitHub Flow**:

- `main` — production-ready, always deployable, protected
- Feature branches branch off `main` and merge back via PR

### Branch Naming

```
feat/42-booking-conflict-detection
fix/37-bot-null-service-crash
chore/update-prisma-client
docs/add-api-reference
```

Always reference the issue number if one exists.

---

## Commit Convention

We use [Conventional Commits](https://www.conventionalcommits.org/).

```
<type>(<scope>): <short description>

[optional body]

[optional footer]
```

### Types

| Type | When to use |
|---|---|
| `feat` | New feature |
| `fix` | Bug fix |
| `chore` | Config, deps, tooling |
| `docs` | Documentation only |
| `test` | Tests only |
| `refactor` | Code change with no feature/fix |
| `perf` | Performance improvement |

### Scopes

`api`, `web`, `bot`, `db`, `infra`, `types`, `ui`, `config`

### Examples

```
feat(api): add POST /bookings endpoint
fix(bot): handle empty service list in WhatsApp FSM
chore(deps): upgrade prisma to 5.10
docs(setup): clarify docker compose instructions
test(booking): add unit tests for slot availability logic
```

---

## Pull Request Process

1. **Branch off `main`** — never commit directly to `main`
2. **Keep PRs small** — one feature or fix per PR
3. **Link the issue** — use `Closes #42` in the PR body
4. **Fill in the PR template** — description, test evidence, checklist
5. **Pass CI** — lint, typecheck, and tests must all pass
6. **Request 1 review** — PRs require 1 approval before merging
7. **Squash and merge** — keep `main` history clean

---

## Code Standards

- TypeScript strict mode — no `any`
- All database queries scoped by `businessId`
- Phone numbers in E.164 format (`+491234567890`)
- Timestamps stored in UTC
- No hardcoded IDs or secrets

---

## Running Tests

```bash
# All tests
pnpm test

# API tests only
pnpm --filter @salonflow/api test

# Watch mode
pnpm --filter @salonflow/api test:watch
```

---

## Linting and Type Checking

```bash
# Lint all packages
pnpm lint

# Type check all packages
pnpm typecheck
```

CI will reject PRs that fail either of these.

---

## Database Changes

All schema changes require a Prisma migration:

```bash
# Create and apply migration
pnpm --filter @salonflow/database db:migrate

# Reset local database (dev only)
pnpm --filter @salonflow/database db:reset
```

Never edit generated files in `packages/database/generated/`.

---

## Questions?

Open a [GitHub Discussion](https://github.com/MLobbos/salonflow/discussions) or ping the team.
