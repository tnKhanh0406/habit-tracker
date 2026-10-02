CREATE TABLE player_characters (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    level INTEGER NOT NULL DEFAULT 1,
    exp BIGINT NOT NULL DEFAULT 0,
    hp INTEGER NOT NULL DEFAULT 100,
    max_hp INTEGER NOT NULL DEFAULT 100,
    mp INTEGER NOT NULL DEFAULT 50,
    max_mp INTEGER NOT NULL DEFAULT 50,
    coin BIGINT NOT NULL DEFAULT 0,
    gem BIGINT NOT NULL DEFAULT 0,
    strength INTEGER NOT NULL DEFAULT 1,
    intelligence INTEGER NOT NULL DEFAULT 1,
    vitality INTEGER NOT NULL DEFAULT 1,
    agility INTEGER NOT NULL DEFAULT 1,
    stat_points INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_character_level CHECK (level >= 1),
    CONSTRAINT chk_character_exp CHECK (exp >= 0),
    CONSTRAINT chk_character_hp CHECK (hp >= 0 AND hp <= max_hp),
    CONSTRAINT chk_character_mp CHECK (mp >= 0 AND mp <= max_mp),
    CONSTRAINT chk_character_coin CHECK (coin >= 0),
    CONSTRAINT chk_character_gem CHECK (gem >= 0),
    CONSTRAINT chk_stat_points CHECK (stat_points >= 0)
);

ALTER TABLE player_characters
ADD CONSTRAINT fk_player_character_user
FOREIGN KEY (user_id)
REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE player_characters
ADD COLUMN max_level_reached INTEGER NOT NULL DEFAULT 1;