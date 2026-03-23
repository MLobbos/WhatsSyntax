#!/usr/bin/env bash
set -euo pipefail

# WhatsSyntax — one-command local dev setup
# Tested on macOS (Intel/Apple Silicon) and Linux (Ubuntu 22.04+)

CYAN='\033[0;36m'; GREEN='\033[0;32m'; RED='\033[0;31m'; NC='\033[0m'

info()  { echo -e "${CYAN}[setup]${NC} $*"; }
ok()    { echo -e "${GREEN}[ok]${NC}   $*"; }
fail()  { echo -e "${RED}[fail]${NC} $*"; exit 1; }

# ── Prerequisites ─────────────────────────────────────────────────────────────
command -v java >/dev/null 2>&1  || fail "Java 17+ required. Install from https://adoptium.net"
command -v docker >/dev/null 2>&1 || fail "Docker required. Install from https://docker.com"
command -v docker compose version >/dev/null 2>&1 || fail "Docker Compose v2 required."

JAVA_VER=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
[[ "$JAVA_VER" -ge 17 ]] || fail "Java 17+ required, found Java $JAVA_VER"

ok "Prerequisites satisfied (Java $JAVA_VER, Docker)"

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

# ── Gradle wrapper ────────────────────────────────────────────────────────────
info "Verifying Gradle wrapper..."
cd "$ROOT_DIR"
./gradlew --version > /dev/null
ok "Gradle wrapper OK"

# ── Start Docker services ─────────────────────────────────────────────────────
info "Starting Postgres + Redis..."
docker compose -f infra/docker-compose.yml up -d postgres redis

info "Waiting for Postgres to be healthy..."
for i in {1..30}; do
    if docker compose -f infra/docker-compose.yml exec -T postgres pg_isready -U postgres > /dev/null 2>&1; then
        ok "Postgres is ready"
        break
    fi
    sleep 1
done

# ── Build shared-models ───────────────────────────────────────────────────────
info "Building shared-models module..."
./gradlew :packages:shared-models:build
ok "shared-models built"

# ── Summary ───────────────────────────────────────────────────────────────────
echo ""
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}  WhatsSyntax dev environment is ready!  ${NC}"
echo -e "${GREEN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo ""
echo "  Postgres:  postgresql://postgres:password@localhost:5432/whatssyntax"
echo "  Redis:     redis://localhost:6379"
echo ""
echo "  Run backend:  ./gradlew :services:api:run"
echo "  Run tests:    ./gradlew test"
echo "  Open Android: Android Studio → Open → $ROOT_DIR"
echo ""
