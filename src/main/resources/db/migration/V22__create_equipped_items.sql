CREATE TABLE equipped_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    inventory_item_id UUID NOT NULL REFERENCES inventory_items(id) ON DELETE CASCADE,
    slot VARCHAR(20) NOT NULL,
    equipped_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_equipped_user_slot UNIQUE (user_id, slot),
    CONSTRAINT uk_equipped_inventory_item UNIQUE (inventory_item_id)
);

CREATE INDEX ix_equipped_items_user ON equipped_items(user_id);
