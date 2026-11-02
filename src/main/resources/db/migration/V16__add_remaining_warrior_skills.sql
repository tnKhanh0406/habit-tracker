ALTER TABLE skill_effects
    ADD COLUMN condition_value NUMERIC(12,2);

INSERT INTO skills (
    id, code, name, description, character_class, skill_type,
    mana_cost, required_level, cooldown_seconds, target_type
)
VALUES
(
    '10000000-0000-0000-0000-000000000007',
    'WARRIOR_STREAK_GUARD',
    'Streak Guard',
    'Protect the selected habit streak from the next missed-day penalty.',
    'WARRIOR', 'ACTIVE', 25, 1, 86400, 'HABIT'
),
(
    '10000000-0000-0000-0000-000000000008',
    'WARRIOR_BATTLE_DISCIPLINE',
    'Battle Discipline',
    'Passively gain 5%, 10%, or 20% bonus EXP at habit streaks 7, 14, or 30.',
    'WARRIOR', 'PASSIVE', 0, 1, 0, 'SELF'
),
(
    '10000000-0000-0000-0000-000000000009',
    'WARRIOR_LAST_STAND',
    'Last Stand',
    'For the selected habit, block 1 HP damage and protect its streak from the next missed-day penalty.',
    'WARRIOR', 'ACTIVE', 35, 1, 86400, 'HABIT'
);

INSERT INTO skill_effects (
    id, skill_id, effect_type, effect_value, condition_value, value_type, duration_type
)
VALUES
('20000000-0000-0000-0000-000000000008', '10000000-0000-0000-0000-000000000007', 'PROTECT_STREAK', 1, NULL, 'FLAT', 'NEXT_PENALTY'),
('20000000-0000-0000-0000-000000000009', '10000000-0000-0000-0000-000000000008', 'STREAK_BONUS_EXP', 5, 7, 'PERCENT', 'PASSIVE'),
('20000000-0000-0000-0000-000000000010', '10000000-0000-0000-0000-000000000008', 'STREAK_BONUS_EXP', 10, 14, 'PERCENT', 'PASSIVE'),
('20000000-0000-0000-0000-000000000011', '10000000-0000-0000-0000-000000000008', 'STREAK_BONUS_EXP', 20, 30, 'PERCENT', 'PASSIVE'),
('20000000-0000-0000-0000-000000000012', '10000000-0000-0000-0000-000000000009', 'BLOCK_HP_PENALTY', 1, NULL, 'FLAT', 'NEXT_PENALTY'),
('20000000-0000-0000-0000-000000000013', '10000000-0000-0000-0000-000000000009', 'PROTECT_STREAK', 1, NULL, 'FLAT', 'NEXT_PENALTY');
