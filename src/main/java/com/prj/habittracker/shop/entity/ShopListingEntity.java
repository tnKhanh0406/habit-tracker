package com.prj.habittracker.shop.entity;

import com.prj.habittracker.item.entity.ItemEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "shop_listings")
@Getter
@Setter
public class ShopListingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(name = "price_coin", nullable = false)
    private Long priceCoin;
    @Column(name = "price_gem", nullable = false)
    private Long priceGem;
    @Column(name = "required_level", nullable = false)
    private Integer requiredLevel;
    private Integer stock;
    @Column(nullable = false)
    private Boolean enabled;
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
