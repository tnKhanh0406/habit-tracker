CREATE TABLE items (
    id UUID PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description TEXT,
    item_type VARCHAR(20) NOT NULL,
    rarity VARCHAR(20) NOT NULL,
    stackable BOOLEAN NOT NULL,
    max_stack INTEGER NOT NULL,
    icon VARCHAR(80),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_item_max_stack CHECK (max_stack >= 1)
);

CREATE TABLE equipment_definitions (
    item_id UUID PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE,
    slot VARCHAR(20) NOT NULL,
    required_class VARCHAR(30),
    required_level INTEGER NOT NULL DEFAULT 1,
    strength_bonus INTEGER NOT NULL DEFAULT 0,
    intelligence_bonus INTEGER NOT NULL DEFAULT 0,
    vitality_bonus INTEGER NOT NULL DEFAULT 0,
    agility_bonus INTEGER NOT NULL DEFAULT 0,
    max_hp_bonus INTEGER NOT NULL DEFAULT 0,
    max_mp_bonus INTEGER NOT NULL DEFAULT 0,
    boss_damage_percent NUMERIC(5,2) NOT NULL DEFAULT 0,
    CONSTRAINT chk_equipment_required_level CHECK (required_level >= 1)
);

CREATE TABLE consumable_definitions (
    item_id UUID PRIMARY KEY REFERENCES items(id) ON DELETE CASCADE,
    effect_type VARCHAR(30) NOT NULL,
    effect_value NUMERIC(12,2) NOT NULL,
    CONSTRAINT chk_consumable_effect_value CHECK (effect_value > 0)
);

CREATE TABLE inventory_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES items(id),
    quantity INTEGER NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0,
    acquired_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_inventory_user_item UNIQUE (user_id, item_id),
    CONSTRAINT chk_inventory_quantity CHECK (quantity >= 0)
);

CREATE INDEX ix_inventory_user ON inventory_items(user_id);

CREATE TABLE inventory_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id UUID NOT NULL REFERENCES items(id),
    quantity_delta INTEGER NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    source_type VARCHAR(40),
    source_id UUID,
    idempotency_key UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_inventory_transaction_delta CHECK (quantity_delta <> 0)
);

CREATE UNIQUE INDEX uk_inventory_transaction_idempotency
    ON inventory_transactions(user_id, idempotency_key)
    WHERE idempotency_key IS NOT NULL;

INSERT INTO items (id, code, name, description, item_type, rarity, stackable, max_stack, icon) VALUES
('30000000-0000-0000-0000-000000000001', 'WARRIOR_IRON_SWORD', 'Iron Sword', 'A reliable blade that grants 3 Strength.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'sword'),
('30000000-0000-0000-0000-000000000002', 'WARRIOR_GUARD_PLATE', 'Guard Plate', 'Sturdy armor that grants 3 Vitality and 10 max HP.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'shield'),
('30000000-0000-0000-0000-000000000003', 'WARRIOR_VANGUARD_EMBLEM', 'Vanguard Emblem', 'Grants 1 Strength, 1 Vitality and 5% boss damage.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'badge'),
('30000000-0000-0000-0000-000000000004', 'MAGE_ASH_WAND', 'Ash Wand', 'A simple wand that grants 3 Intelligence.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'wand'),
('30000000-0000-0000-0000-000000000005', 'MAGE_SCHOLAR_ROBE', 'Scholar Robe', 'Grants 2 Intelligence and 10 max MP.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'robe'),
('30000000-0000-0000-0000-000000000006', 'MAGE_ARCANE_RING', 'Arcane Ring', 'Grants 1 Intelligence and 5 max MP.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'ring'),
('30000000-0000-0000-0000-000000000007', 'RANGER_LONGBOW', 'Longbow', 'A balanced bow that grants 3 Agility.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'bow-arrow'),
('30000000-0000-0000-0000-000000000008', 'RANGER_TRAIL_JACKET', 'Trail Jacket', 'Grants 2 Agility and 1 Vitality.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'shirt'),
('30000000-0000-0000-0000-000000000009', 'RANGER_HAWK_CHARM', 'Hawk Charm', 'Grants 1 Agility and 5% boss damage.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'feather'),
('30000000-0000-0000-0000-000000000010', 'CLERIC_OAK_STAFF', 'Oak Staff', 'Grants 2 Intelligence and 1 Vitality.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'staff'),
('30000000-0000-0000-0000-000000000011', 'CLERIC_ACOLYTE_ROBE', 'Acolyte Robe', 'Grants 2 Vitality and 5 max MP.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'robe'),
('30000000-0000-0000-0000-000000000012', 'CLERIC_BLESSED_PENDANT', 'Blessed Pendant', 'Grants 5 max HP and 5 max MP.', 'EQUIPMENT', 'COMMON', FALSE, 1, 'gem'),
('30000000-0000-0000-0000-000000000013', 'MINOR_HEALTH_POTION', 'Minor Health Potion', 'Restores 20 HP.', 'CONSUMABLE', 'COMMON', TRUE, 20, 'heart'),
('30000000-0000-0000-0000-000000000014', 'HEALTH_POTION', 'Health Potion', 'Restores 40 HP.', 'CONSUMABLE', 'UNCOMMON', TRUE, 20, 'heart-pulse'),
('30000000-0000-0000-0000-000000000015', 'MINOR_MANA_POTION', 'Minor Mana Potion', 'Restores 15 MP.', 'CONSUMABLE', 'COMMON', TRUE, 20, 'droplet'),
('30000000-0000-0000-0000-000000000016', 'MANA_POTION', 'Mana Potion', 'Restores 30 MP.', 'CONSUMABLE', 'UNCOMMON', TRUE, 20, 'flask-conical');

INSERT INTO equipment_definitions
(item_id, slot, required_class, strength_bonus, intelligence_bonus, vitality_bonus, agility_bonus, max_hp_bonus, max_mp_bonus, boss_damage_percent) VALUES
('30000000-0000-0000-0000-000000000001', 'WEAPON', 'WARRIOR', 3, 0, 0, 0, 0, 0, 0),
('30000000-0000-0000-0000-000000000002', 'ARMOR', 'WARRIOR', 0, 0, 3, 0, 10, 0, 0),
('30000000-0000-0000-0000-000000000003', 'ACCESSORY', 'WARRIOR', 1, 0, 1, 0, 0, 0, 5),
('30000000-0000-0000-0000-000000000004', 'WEAPON', 'MAGE', 0, 3, 0, 0, 0, 0, 0),
('30000000-0000-0000-0000-000000000005', 'ARMOR', 'MAGE', 0, 2, 0, 0, 0, 10, 0),
('30000000-0000-0000-0000-000000000006', 'ACCESSORY', 'MAGE', 0, 1, 0, 0, 0, 5, 0),
('30000000-0000-0000-0000-000000000007', 'WEAPON', 'RANGER', 0, 0, 0, 3, 0, 0, 0),
('30000000-0000-0000-0000-000000000008', 'ARMOR', 'RANGER', 0, 0, 1, 2, 0, 0, 0),
('30000000-0000-0000-0000-000000000009', 'ACCESSORY', 'RANGER', 0, 0, 0, 1, 0, 0, 5),
('30000000-0000-0000-0000-000000000010', 'WEAPON', 'CLERIC', 0, 2, 1, 0, 0, 0, 0),
('30000000-0000-0000-0000-000000000011', 'ARMOR', 'CLERIC', 0, 0, 2, 0, 0, 5, 0),
('30000000-0000-0000-0000-000000000012', 'ACCESSORY', 'CLERIC', 0, 0, 0, 0, 5, 5, 0);

INSERT INTO consumable_definitions (item_id, effect_type, effect_value) VALUES
('30000000-0000-0000-0000-000000000013', 'RESTORE_HP', 20),
('30000000-0000-0000-0000-000000000014', 'RESTORE_HP', 40),
('30000000-0000-0000-0000-000000000015', 'RESTORE_MP', 15),
('30000000-0000-0000-0000-000000000016', 'RESTORE_MP', 30);

INSERT INTO inventory_items (user_id, item_id, quantity)
SELECT pc.user_id, starter.item_id, starter.quantity
FROM player_characters pc
JOIN (VALUES
    ('WARRIOR', '30000000-0000-0000-0000-000000000001'::UUID, 1),
    ('WARRIOR', '30000000-0000-0000-0000-000000000002'::UUID, 1),
    ('WARRIOR', '30000000-0000-0000-0000-000000000003'::UUID, 1),
    ('MAGE', '30000000-0000-0000-0000-000000000004'::UUID, 1),
    ('MAGE', '30000000-0000-0000-0000-000000000005'::UUID, 1),
    ('MAGE', '30000000-0000-0000-0000-000000000006'::UUID, 1),
    ('RANGER', '30000000-0000-0000-0000-000000000007'::UUID, 1),
    ('RANGER', '30000000-0000-0000-0000-000000000008'::UUID, 1),
    ('RANGER', '30000000-0000-0000-0000-000000000009'::UUID, 1),
    ('CLERIC', '30000000-0000-0000-0000-000000000010'::UUID, 1),
    ('CLERIC', '30000000-0000-0000-0000-000000000011'::UUID, 1),
    ('CLERIC', '30000000-0000-0000-0000-000000000012'::UUID, 1)
) AS starter(character_class, item_id, quantity) ON starter.character_class = pc.character_class
ON CONFLICT (user_id, item_id) DO NOTHING;

INSERT INTO inventory_items (user_id, item_id, quantity)
SELECT pc.user_id, potion.item_id, 2
FROM player_characters pc
CROSS JOIN (VALUES
    ('30000000-0000-0000-0000-000000000013'::UUID),
    ('30000000-0000-0000-0000-000000000015'::UUID)
) AS potion(item_id)
WHERE pc.character_class IS NOT NULL
ON CONFLICT (user_id, item_id) DO NOTHING;

INSERT INTO inventory_transactions (user_id, item_id, quantity_delta, transaction_type, source_type)
SELECT user_id, item_id, quantity, 'STARTER_GRANT', 'MIGRATION'
FROM inventory_items
WHERE quantity > 0;
