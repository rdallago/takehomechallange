CREATE TABLE notifications (
    id         UUID PRIMARY KEY,
    user_id    UUID         NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title      VARCHAR(150) NOT NULL,
    content    TEXT         NOT NULL,
    channel    VARCHAR(20)  NOT NULL,
    recipient  VARCHAR(255) NOT NULL, -- email, telefono o device token segun el canal
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_notifications_channel CHECK (channel IN ('EMAIL', 'SMS', 'PUSH'))
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);