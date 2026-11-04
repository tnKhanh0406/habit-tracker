package com.prj.habittracker.inventory.entity;

import com.prj.habittracker.common.enums.EquipmentSlot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipped_items", uniqueConstraints = {
        @UniqueConstraint(name = "uk_equipped_user_slot", columnNames = {"user_id", "slot"}),
        @UniqueConstraint(name = "uk_equipped_inventory_item", columnNames = "inventory_item_id")
})
@Getter
@Setter
public class EquippedItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItemEntity inventoryItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentSlot slot;

    @CreationTimestamp
    @Column(name = "equipped_at", nullable = false, updatable = false)
    private OffsetDateTime equippedAt;
}
