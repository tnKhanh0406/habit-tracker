INSERT INTO skills (id, code, name, description, character_class, skill_type, mana_cost, required_level, cooldown_seconds, target_type)
VALUES
('10000000-0000-0000-0000-000000000015', 'CLERIC_BLESSING', 'Blessing', 'Reduce the next login HP penalty by 50%, rounding remaining damage up.', 'CLERIC', 'ACTIVE', 25, 1, 86400, 'SELF'),
('10000000-0000-0000-0000-000000000016', 'CLERIC_RECOVERY', 'Recovery', 'Restore 10 HP on the next habit or todo completion.', 'CLERIC', 'ACTIVE', 20, 1, 86400, 'NEXT_COMPLETION');

INSERT INTO skill_effects (id, skill_id, effect_type, effect_value, value_type, duration_type)
VALUES
('20000000-0000-0000-0000-000000000021', '10000000-0000-0000-0000-000000000015', 'REDUCE_HP_PENALTY', 50, 'PERCENT', 'NEXT_PENALTY'),
('20000000-0000-0000-0000-000000000022', '10000000-0000-0000-0000-000000000016', 'HEAL_HP_AFTER_COMPLETION', 10, 'FLAT', 'NEXT_COMPLETION');
