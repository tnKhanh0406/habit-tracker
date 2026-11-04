package com.prj.habittracker.shop.entity;

import com.prj.habittracker.item.entity.ItemEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "shop_purchases", uniqueConstraints =
        @UniqueConstraint(name = "uk_shop_purchase_idempotency", columnNames = {"user_id", "idempotency_key"}))
@Getter
@Setter
public class ShopPurchaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private ShopListingEntity listing;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;
    @Column(nullable = false)
    private Integer quantity;
    @Column(name = "unit_coin_price", nullable = false)
    private Long unitCoinPrice;
    @Column(name = "unit_gem_price", nullable = false)
    private Long unitGemPrice;
    @Column(name = "total_coin", nullable = false)
    private Long totalCoin;
    @Column(name = "total_gem", nullable = false)
    private Long totalGem;
    @Column(name = "remaining_coin", nullable = false)
    private Long remainingCoin;
    @Column(name = "remaining_gem", nullable = false)
    private Long remainingGem;
    @Column(name = "resulting_inventory_quantity", nullable = false)
    private Integer resultingInventoryQuantity;
    @Column(name = "idempotency_key", nullable = false)
    private UUID idempotencyKey;
    @CreationTimestamp
    @Column(name = "purchased_at", nullable = false, updatable = false)
    private OffsetDateTime purchasedAt;
}
