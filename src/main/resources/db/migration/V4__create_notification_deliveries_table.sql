CREATE TABLE notification_deliveries (
    id              UUID PRIMARY KEY,
    notification_id UUID         NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    channel         VARCHAR(20)  NOT NULL,
    status          VARCHAR(20)  NOT NULL, -- SENT / FAILED
    detail          TEXT,                  -- payload, template usado, motivo del error, etc.
    sent_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_deliveries_status CHECK (status IN ('SENT', 'FAILED'))
);

CREATE INDEX idx_deliveries_notification_id ON notification_deliveries(notification_id);