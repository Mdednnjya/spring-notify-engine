CREATE TABLE IF NOT EXISTS notification_events (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key  VARCHAR(255) NOT NULL UNIQUE,
    recipient        VARCHAR(255) NOT NULL,
    type             VARCHAR(50)  NOT NULL,
    payload          TEXT,
    status           VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
    retry_count      INT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);
