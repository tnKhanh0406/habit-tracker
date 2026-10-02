CREATE TABLE rpg_reward_grants (
    id UUID PRIMARY KEY,

    user_id UUID NOT NULL,

    source_type VARCHAR(50) NOT NULL,
    source_id UUID NOT NULL,

    exp_amount BIGINT NOT NULL,
    coin_amount BIGINT NOT NULL,

    streak_snapshot INTEGER,

    status VARCHAR(20) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL,
    reversed_at TIMESTAMPTZ,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uk_rpg_reward_source UNIQUE (source_type, source_id)
);