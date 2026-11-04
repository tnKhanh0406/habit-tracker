package com.prj.habittracker.inventory.entity;

import com.prj.habittracker.item.entity.ItemEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_items", uniqueConstraints =
        @UniqueConstraint(name = "uk_inventory_user_item", columnNames = {"user_id", "item_id"}))
@Getter
@Setter
public class InventoryItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(nullable = false)
    private Integer quantity;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "acquired_at", nullable = false, updatable = false)
    private OffsetDateTime acquiredAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
