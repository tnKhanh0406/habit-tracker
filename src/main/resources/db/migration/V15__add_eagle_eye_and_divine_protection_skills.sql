INSERT INTO skills (
    id, code, name, description, character_class, skill_type,
    mana_cost, required_level, cooldown_seconds, target_type
)
VALUES
(
    '10000000-0000-0000-0000-000000000005',
    'RANGER_EAGLE_EYE',
    'Eagle Eye',
    'Double both EXP and coin when the selected todo is completed.',
    'RANGER', 'ACTIVE', 30, 1, 86400, 'TODO'
),
(
    '10000000-0000-0000-0000-000000000006',
    'CLERIC_DIVINE_PROTECTION',
    'Divine Protection',
    'Restore 20 HP immediately. Each point of overhealing becomes 1 coin.',
    'CLERIC', 'ACTIVE', 30, 1, 86400, 'SELF'
);

INSERT INTO skill_effects (
    id, skill_id, effect_type, effect_value, value_type, duration_type
)
VALUES
(
    '20000000-0000-0000-0000-000000000005',
    '10000000-0000-0000-0000-000000000005',
    'BONUS_EXP', 100, 'PERCENT', 'NEXT_COMPLETION'
),
(
    '20000000-0000-0000-0000-000000000006',
    '10000000-0000-0000-0000-000000000005',
    'BONUS_COIN', 100, 'PERCENT', 'NEXT_COMPLETION'
),
(
    '20000000-0000-0000-0000-000000000007',
    '10000000-0000-0000-0000-000000000006',
    'OVERHEAL_TO_COIN', 20, 'FLAT', 'INSTANT'
);
