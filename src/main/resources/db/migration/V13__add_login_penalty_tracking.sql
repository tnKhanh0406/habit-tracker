ALTER TABLE users
ADD COLUMN last_login_date DATE;

CREATE TABLE rpg_penalty_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    source_type VARCHAR(40) NOT NULL,
    source_id UUID NOT NULL,
    penalty_date DATE NOT NULL,
    hp_amount INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_rpg_penalty_hp_amount CHECK (hp_amount > 0),
    CONSTRAINT uk_rpg_penalty_source UNIQUE (source_type, source_id)
);