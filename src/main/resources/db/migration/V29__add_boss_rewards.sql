CREATE TABLE boss_rewards (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    boss_definition_id UUID NOT NULL REFERENCES boss_definitions(id) ON DELETE CASCADE,
    reward_type VARCHAR(20) NOT NULL,
    amount BIGINT NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT uk_boss_reward_type UNIQUE (boss_definition_id, reward_type),
    CONSTRAINT chk_boss_reward_amount CHECK (amount > 0)
);

CREATE TABLE boss_reward_grants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    battle_id UUID NOT NULL REFERENCES party_boss_battles(id) ON DELETE CASCADE,
    party_id UUID NOT NULL REFERENCES parties(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id),
    exp_amount BIGINT NOT NULL DEFAULT 0,
    coin_amount BIGINT NOT NULL DEFAULT 0,
    gem_amount BIGINT NOT NULL DEFAULT 0,
    damage_contribution BIGINT NOT NULL,
    granted_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_boss_reward_grant UNIQUE (battle_id, user_id),
    CONSTRAINT chk_boss_grant_amounts CHECK (exp_amount >= 0 AND coin_amount >= 0 AND gem_amount >= 0),
    CONSTRAINT chk_boss_grant_contribution CHECK (damage_contribution > 0)
);

CREATE INDEX ix_boss_reward_grants_user ON boss_reward_grants(user_id, granted_at DESC);

INSERT INTO boss_rewards (boss_definition_id, reward_type, amount, sort_order) VALUES
('50000000-0000-0000-0000-000000000001', 'EXP', 100, 10),
('50000000-0000-0000-0000-000000000001', 'COIN', 75, 20),
('50000000-0000-0000-0000-000000000002', 'EXP', 250, 10),
('50000000-0000-0000-0000-000000000002', 'COIN', 150, 20),
('50000000-0000-0000-0000-000000000003', 'EXP', 500, 10),
('50000000-0000-0000-0000-000000000003', 'COIN', 300, 20),
('50000000-0000-0000-0000-000000000003', 'GEM', 1, 30),
('50000000-0000-0000-0000-000000000004', 'EXP', 900, 10),
('50000000-0000-0000-0000-000000000004', 'COIN', 550, 20),
('50000000-0000-0000-0000-000000000004', 'GEM', 2, 30),
('50000000-0000-0000-0000-000000000005', 'EXP', 1500, 10),
('50000000-0000-0000-0000-000000000005', 'COIN', 1000, 20),
('50000000-0000-0000-0000-000000000005', 'GEM', 5, 30);
