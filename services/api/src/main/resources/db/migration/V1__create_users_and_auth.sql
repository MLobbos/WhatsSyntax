-- V1: create users table
CREATE TABLE users (
    id          TEXT        PRIMARY KEY,
    phone       TEXT        NOT NULL UNIQUE,
    display_name TEXT       NOT NULL DEFAULT 'New User',
    avatar_url  TEXT,
    status_text TEXT,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_users_phone ON users(phone);

-- V1: OTP store (ephemeral — TTL enforced by the application)
CREATE TABLE otp_codes (
    phone       TEXT        PRIMARY KEY,
    code        TEXT        NOT NULL,
    expires_at  TIMESTAMPTZ NOT NULL
);

-- V1: refresh tokens
CREATE TABLE refresh_tokens (
    id          TEXT        PRIMARY KEY,
    user_id     TEXT        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash  TEXT        NOT NULL UNIQUE,
    expires_at  TIMESTAMPTZ NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
