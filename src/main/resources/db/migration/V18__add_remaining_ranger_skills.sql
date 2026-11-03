CREATE INDEX ix_todos_user_completed_at ON todos(user_id, completed_at);

INSERT INTO skills (
    id, code, name, description, character_class, skill_type,
    mana_cost, required_level, cooldown_seconds, target_type
)
VALUES
(
    '10000000-0000-0000-0000-000000000013',
    'RANGER_QUICK_STEP',
    'Quick Step',
    'Gain 25% bonus EXP and coin if the selected todo is completed before its deadline.',
    'RANGER', 'ACTIVE', 20, 1, 86400, 'TODO'
),
(
    '10000000-0000-0000-0000-000000000014',
    'RANGER_MOMENTUM',
    'Momentum',
    'Passively gain 5% bonus EXP and coin per earlier todo completed today, up to 25%.',
    'RANGER', 'PASSIVE', 0, 1, 0, 'SELF'
);

INSERT INTO skill_effects (
    id, skill_id, effect_type, effect_value, condition_value, value_type, duration_type
)
VALUES
('20000000-0000-0000-0000-000000000018', '10000000-0000-0000-0000-000000000013', 'EARLY_COMPLETION_BONUS_EXP', 25, NULL, 'PERCENT', 'NEXT_COMPLETION'),
('20000000-0000-0000-0000-000000000019', '10000000-0000-0000-0000-000000000013', 'EARLY_COMPLETION_BONUS_COIN', 25, NULL, 'PERCENT', 'NEXT_COMPLETION'),
('20000000-0000-0000-0000-000000000020', '10000000-0000-0000-0000-000000000014', 'DAILY_COMBO_BONUS_REWARD', 5, 25, 'PERCENT', 'PASSIVE');
