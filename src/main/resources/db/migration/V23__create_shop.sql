CREATE TABLE shop_listings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    item_id UUID NOT NULL REFERENCES items(id),
    price_coin BIGINT NOT NULL DEFAULT 0,
    price_gem BIGINT NOT NULL DEFAULT 0,
    required_level INTEGER NOT NULL DEFAULT 1,
    stock INTEGER,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_shop_listing_price CHECK (price_coin >= 0 AND price_gem >= 0 AND (price_coin > 0 OR price_gem > 0)),
    CONSTRAINT chk_shop_listing_level CHECK (required_level >= 1),
    CONSTRAINT chk_shop_listing_stock CHECK (stock IS NULL OR stock >= 0)
);

CREATE TABLE shop_purchases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    listing_id UUID NOT NULL REFERENCES shop_listings(id),
    item_id UUID NOT NULL REFERENCES items(id),
    quantity INTEGER NOT NULL,
    unit_coin_price BIGINT NOT NULL,
    unit_gem_price BIGINT NOT NULL,
    total_coin BIGINT NOT NULL,
    total_gem BIGINT NOT NULL,
    remaining_coin BIGINT NOT NULL,
    remaining_gem BIGINT NOT NULL,
    resulting_inventory_quantity INTEGER NOT NULL,
    idempotency_key UUID NOT NULL,
    purchased_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_shop_purchase_idempotency UNIQUE (user_id, idempotency_key),
    CONSTRAINT chk_shop_purchase_quantity CHECK (quantity > 0)
);

CREATE INDEX ix_shop_listings_enabled_sort ON shop_listings(enabled, sort_order);
CREATE INDEX ix_shop_purchases_user_date ON shop_purchases(user_id, purchased_at DESC);

INSERT INTO shop_listings (item_id, price_coin, required_level, sort_order) VALUES
('30000000-0000-0000-0000-000000000013', 25, 1, 10),
('30000000-0000-0000-0000-000000000015', 25, 1, 20),
('30000000-0000-0000-0000-000000000014', 55, 3, 30),
('30000000-0000-0000-0000-000000000016', 55, 3, 40),
('30000000-0000-0000-0000-000000000001', 120, 1, 100),
('30000000-0000-0000-0000-000000000002', 150, 1, 110),
('30000000-0000-0000-0000-000000000003', 180, 1, 120),
('30000000-0000-0000-0000-000000000004', 120, 1, 200),
('30000000-0000-0000-0000-000000000005', 150, 1, 210),
('30000000-0000-0000-0000-000000000006', 180, 1, 220),
('30000000-0000-0000-0000-000000000007', 120, 1, 300),
('30000000-0000-0000-0000-000000000008', 150, 1, 310),
('30000000-0000-0000-0000-000000000009', 180, 1, 320),
('30000000-0000-0000-0000-000000000010', 120, 1, 400),
('30000000-0000-0000-0000-000000000011', 150, 1, 410),
('30000000-0000-0000-0000-000000000012', 180, 1, 420);
