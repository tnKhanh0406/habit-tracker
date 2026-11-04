WITH equipped_bonuses AS (
    SELECT ei.user_id,
           COALESCE(SUM(ed.strength_bonus), 0) AS strength_bonus,
           COALESCE(SUM(ed.intelligence_bonus), 0) AS intelligence_bonus,
           COALESCE(SUM(ed.vitality_bonus), 0) AS vitality_bonus,
           COALESCE(SUM(ed.agility_bonus), 0) AS agility_bonus,
           COALESCE(SUM(ed.max_hp_bonus), 0) AS max_hp_bonus,
           COALESCE(SUM(ed.max_mp_bonus), 0) AS max_mp_bonus
    FROM equipped_items ei
    JOIN inventory_items ii ON ii.id = ei.inventory_item_id
    JOIN equipment_definitions ed ON ed.item_id = ii.item_id
    GROUP BY ei.user_id
)
UPDATE player_characters pc
SET strength = GREATEST(1, pc.strength - eb.strength_bonus),
    intelligence = GREATEST(1, pc.intelligence - eb.intelligence_bonus),
    vitality = GREATEST(1, pc.vitality - eb.vitality_bonus),
    agility = GREATEST(1, pc.agility - eb.agility_bonus),
    max_hp = GREATEST(1, pc.max_hp - eb.max_hp_bonus),
    max_mp = GREATEST(1, pc.max_mp - eb.max_mp_bonus)
FROM equipped_bonuses eb
WHERE pc.user_id = eb.user_id;
