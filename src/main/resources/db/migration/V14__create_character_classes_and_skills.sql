ALTER TABLE player_characters
    ADD COLUMN character_class VARCHAR(30),
    ADD COLUMN mana_regenerated_at DATE NOT NULL DEFAULT CURRENT_DATE;

CREATE TABLE skills (
    id UUID PRIMARY KEY,
    code VARCHAR(60) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    character_class VARCHAR(30) NOT NULL,
    skill_type VARCHAR(20) NOT NULL,
    mana_cost INTEGER NOT NULL,
    required_level INTEGER NOT NULL DEFAULT 1,
    cooldown_seconds INTEGER NOT NULL DEFAULT 0,
    target_type VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_skill_mana_cost CHECK (mana_cost >= 0),
    CONSTRAINT chk_skill_required_level CHECK (required_level >= 1)
);

CREATE TABLE skill_effects (
    id UUID PRIMARY KEY,
    skill_id UUID NOT NULL REFERENCES skills(id) ON DELETE CASCADE,
    effect_type VARCHAR(40) NOT NULL,
    effect_value NUMERIC(12,2) NOT NULL,
    value_type VARCHAR(20) NOT NULL,
    duration_type VARCHAR(30) NOT NULL
);

CREATE TABLE skill_casts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    character_id UUID NOT NULL REFERENCES player_characters(id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(id),
    target_type VARCHAR(30) NOT NULL,
    target_id UUID,
    mana_spent INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    cast_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ,
    consumed_at TIMESTAMPTZ,
    idempotency_key UUID NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uk_skill_cast_idempotency UNIQUE (user_id, idempotency_key)
);

CREATE INDEX ix_skill_cast_active
    ON skill_casts(user_id, status, expires_at);

INSERT INTO skills (id, code, name, description, character_class, skill_type, mana_cost, required_level, cooldown_seconds, target_type)
VALUES
('10000000-0000-0000-0000-000000000001', 'WARRIOR_IRON_WILL', 'Iron Will', 'Block 1 HP damage from the next login penalty.', 'WARRIOR', 'ACTIVE', 15, 1, 86400, 'SELF'),
('10000000-0000-0000-0000-000000000002', 'MAGE_FOCUS', 'Focus', 'Gain 25% bonus EXP from the next habit or todo completion.', 'MAGE', 'ACTIVE', 20, 1, 86400, 'NEXT_COMPLETION'),
('10000000-0000-0000-0000-000000000003', 'RANGER_BOUNTY', 'Bounty', 'Gain 50% bonus coin when the selected todo is completed.', 'RANGER', 'ACTIVE', 15, 1, 86400, 'TODO'),
('10000000-0000-0000-0000-000000000004', 'CLERIC_HEAL', 'Heal', 'Restore 20 HP immediately.', 'CLERIC', 'ACTIVE', 20, 1, 86400, 'SELF');

INSERT INTO skill_effects (id, skill_id, effect_type, effect_value, value_type, duration_type)
VALUES
('20000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001', 'BLOCK_HP_PENALTY', 1, 'FLAT', 'NEXT_PENALTY'),
('20000000-0000-0000-0000-000000000002', '10000000-0000-0000-0000-000000000002', 'BONUS_EXP', 25, 'PERCENT', 'NEXT_COMPLETION'),
('20000000-0000-0000-0000-000000000003', '10000000-0000-0000-0000-000000000003', 'BONUS_COIN', 50, 'PERCENT', 'NEXT_COMPLETION'),
('20000000-0000-0000-0000-000000000004', '10000000-0000-0000-0000-000000000004', 'HEAL_HP', 20, 'FLAT', 'INSTANT');
