-- max_hp and max_mp now store base values. Current HP/MP may legitimately be
-- greater than those columns when equipped items increase the effective maximum.
ALTER TABLE player_characters
    DROP CONSTRAINT IF EXISTS chk_character_hp,
    DROP CONSTRAINT IF EXISTS chk_character_mp;

ALTER TABLE player_characters
    ADD CONSTRAINT chk_character_hp CHECK (hp >= 0),
    ADD CONSTRAINT chk_character_mp CHECK (mp >= 0),
    ADD CONSTRAINT chk_character_max_hp CHECK (max_hp > 0),
    ADD CONSTRAINT chk_character_max_mp CHECK (max_mp > 0);
