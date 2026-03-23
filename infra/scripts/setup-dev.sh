#!/usr/bin/env bash
set -euo pipefail

# SalonFlow — one-command local dev setup
# Tested on macOS (Intel/Apple Silicon) and Linux (Ubuntu 22.04+)

CYAN='\033[0;36m'; GREEN='\033[0;32m'; RED='\033[0;31m'; NC='\033[0m'

info()  { echo -e "${CYAN}[setup]${NC} $*"; }
ok()    { echo -e "${GREEN}[ok]${NC}   $*"; }
fail()  { echo -e "${RED}[fail]${NC} $*"; exit 1; }

# ── Prerequisites ─────────────────────────────────────────────────────────────
command -v node >/dev/null 2>&1  || fail "Node.js 20+ required. Install from https://nodejs.org"
command -v pnpm >/dev/null 2>&1  || fail "pnpm 9+ required. Install: npm install -g pnpm"
command -v docker >/dev/null 2>&1 || fail "Docker required. Install from https://docker.com"
command -v docker compose version >/dev/null 2>&1 || fail "Docker Compose v2 required."

NODE_VER=$(node --version | cut -d. -f1 | tr -d 'v')
[[ "$NODE_VER" -ge 20 ]] || fail "Node.js 20+ required, found Node $NODE_VER"

ok "Prerequisites satisfied (Node $NODE_VER, pnpm, Docker)"

# ── Environment ───────────────────────────────────────────────────────────────
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/../.." && pwd)"

info "Project root: $ROOT_DIR"

# Copy .env.example → .env if not present
if [[ ! -f "$ROOT_DIR/.env" ]]; then
    cp "$ROOT_DIR/.env.example" "$ROOT_DIR/.env"
    ok "Created .env from .env.example — review and update secrets before running in production"
else
    info ".env already exists, skipping copy"
fi

# ── Install dependencies ──────────────────────────────────────────────────────
info "Installing dependencies..."
cd "$ROOT_DIR"
pnpm install
ok "Dependencies installed"

# ── Start Docker services ─────────────────────────────────────────────────────
info "Starting Postgres + Redis..."
docker compose -f infra/docker/docker-compose.yml up -d postgres redis

info "Waiting for Postgres to be healthy..."
for i in {1..30}; do
    if docker compose -f infra/docker/docker-compose.yml exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
        ok "Postgres is ready"
        break
    fi
    sleep 1
done

# ── Database migration ────────────────────────────────────────────────────────
info "Running database migrations..."
pnpm --filter @salonflow/database db:migrate
ok "Database ready"

# ── Summary ───────────────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}  SalonFlow dev environment is ready!    ${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "  Postgres:  postgresql://postgres:password@localhost:5432/salonflow"
echo "  Redis:     redis://localhost:6379"
echo ""
echo "  Start all: pnpm dev"
echo "  API:       http://localhost:3001"
echo "  Web:       http://localhost:3000"
echo "  Swagger:   http://localhost:3001/docs"
echo ""
