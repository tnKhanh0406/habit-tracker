ALTER TABLE player_characters ADD COLUMN class_changed_at TIMESTAMPTZ;
UPDATE player_characters SET class_changed_at = NOW() WHERE character_class IS NOT NULL;
