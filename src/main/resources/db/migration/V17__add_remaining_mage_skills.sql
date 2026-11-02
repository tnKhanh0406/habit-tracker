INSERT INTO skills (
    id, code, name, description, character_class, skill_type,
    mana_cost, required_level, cooldown_seconds, target_type
)
VALUES
(
    '10000000-0000-0000-0000-000000000010',
    'MAGE_ARCANE_SURGE',
    'Arcane Surge',
    'Gain 25% bonus EXP and 25% bonus coin from the next habit or todo completion.',
    'MAGE', 'ACTIVE', 30, 1, 86400, 'NEXT_COMPLETION'
),
(
    '10000000-0000-0000-0000-000000000011',
    'MAGE_MEDITATION',
    'Meditation',
    'Restore 20 MP immediately.',
    'MAGE', 'ACTIVE', 0, 1, 86400, 'SELF'
),
(
    '10000000-0000-0000-0000-000000000012',
    'MAGE_SCHOLARS_WISDOM',
    'Scholar''s Wisdom',
    'Passively gain 15% bonus EXP from HARD and EXTREME habit completions.',
    'MAGE', 'PASSIVE', 0, 1, 0, 'SELF'
);

INSERT INTO skill_effects (
    id, skill_id, effect_type, effect_value, condition_value, value_type, duration_type
)
VALUES
('20000000-0000-0000-0000-000000000014', '10000000-0000-0000-0000-000000000010', 'BONUS_EXP', 25, NULL, 'PERCENT', 'NEXT_COMPLETION'),
('20000000-0000-0000-0000-000000000015', '10000000-0000-0000-0000-000000000010', 'BONUS_COIN', 25, NULL, 'PERCENT', 'NEXT_COMPLETION'),
('20000000-0000-0000-0000-000000000016', '10000000-0000-0000-0000-000000000011', 'RESTORE_MANA', 20, NULL, 'FLAT', 'INSTANT'),
('20000000-0000-0000-0000-000000000017', '10000000-0000-0000-0000-000000000012', 'DIFFICULT_HABIT_BONUS_EXP', 15, NULL, 'PERCENT', 'PASSIVE');
