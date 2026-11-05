CREATE TABLE boss_definitions (
    id UUID PRIMARY KEY,
    code VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    difficulty VARCHAR(20) NOT NULL,
    sequence_order INTEGER NOT NULL UNIQUE,
    base_hp INTEGER NOT NULL,
    icon VARCHAR(60),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_boss_sequence CHECK (sequence_order >= 1),
    CONSTRAINT chk_boss_base_hp CHECK (base_hp > 0)
);

CREATE TABLE party_boss_battles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    party_id UUID NOT NULL REFERENCES parties(id) ON DELETE CASCADE,
    boss_definition_id UUID NOT NULL REFERENCES boss_definitions(id),
    started_by_user_id UUID NOT NULL REFERENCES users(id),
    status VARCHAR(20) NOT NULL,
    max_hp INTEGER NOT NULL,
    current_hp INTEGER NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    started_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    defeated_at TIMESTAMPTZ,
    CONSTRAINT uk_party_boss_once UNIQUE (party_id, boss_definition_id),
    CONSTRAINT chk_party_boss_hp CHECK (max_hp > 0 AND current_hp >= 0 AND current_hp <= max_hp)
);

CREATE UNIQUE INDEX uk_party_active_boss
    ON party_boss_battles(party_id)
    WHERE status = 'ACTIVE';

CREATE INDEX ix_party_boss_battles_party ON party_boss_battles(party_id, started_at DESC);

INSERT INTO boss_definitions
(id, code, name, description, difficulty, sequence_order, base_hp, icon) VALUES
('50000000-0000-0000-0000-000000000001', 'SLIME_KING', 'Slime King',
 'A swollen monarch of the lowlands. The first trial for every new party.', 'EASY', 1, 500, 'crown'),
('50000000-0000-0000-0000-000000000002', 'FOREST_GOLEM', 'Forest Golem',
 'An ancient guardian formed from roots, stone and forgotten magic.', 'NORMAL', 2, 1200, 'trees'),
('50000000-0000-0000-0000-000000000003', 'ABYSSAL_SPIDER', 'Abyssal Spider',
 'A patient hunter whose web stretches through the deepest caverns.', 'HARD', 3, 2500, 'spider'),
('50000000-0000-0000-0000-000000000004', 'FROST_WYRM', 'Frost Wyrm',
 'A winged terror that freezes the resolve of unprepared adventurers.', 'EXTREME', 4, 5000, 'snowflake'),
('50000000-0000-0000-0000-000000000005', 'ANCIENT_DRAGON', 'Ancient Dragon',
 'The final guardian, undefeated across countless generations.', 'LEGENDARY', 5, 10000, 'flame');
