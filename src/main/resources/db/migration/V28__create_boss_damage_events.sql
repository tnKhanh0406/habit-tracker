CREATE TABLE boss_damage_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    battle_id UUID NOT NULL REFERENCES party_boss_battles(id) ON DELETE CASCADE,
    party_id UUID NOT NULL REFERENCES parties(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    source_type VARCHAR(30) NOT NULL,
    source_id UUID NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    base_damage INTEGER NOT NULL,
    main_stat_snapshot INTEGER NOT NULL,
    boss_damage_percent_snapshot NUMERIC(7,2) NOT NULL,
    damage INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    reversed_at TIMESTAMPTZ,
    CONSTRAINT uk_boss_damage_source UNIQUE (source_type, source_id),
    CONSTRAINT chk_boss_damage_positive CHECK (base_damage > 0 AND damage > 0),
    CONSTRAINT chk_boss_damage_main_stat CHECK (main_stat_snapshot >= 0)
);

CREATE INDEX ix_boss_damage_battle ON boss_damage_events(battle_id, created_at DESC);
CREATE INDEX ix_boss_damage_party_user ON boss_damage_events(party_id, user_id);
