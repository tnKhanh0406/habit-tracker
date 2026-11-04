package com.prj.habittracker.inventory.entity;

import com.prj.habittracker.common.enums.InventoryTransactionType;
import com.prj.habittracker.item.entity.ItemEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_transactions")
@Getter
@Setter
public class InventoryTransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private ItemEntity item;

    @Column(name = "quantity_delta", nullable = false)
    private Integer quantityDelta;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 30)
    private InventoryTransactionType transactionType;

    @Column(name = "source_type", length = 40)
    private String sourceType;

    @Column(name = "source_id")
    private UUID sourceId;

    @Column(name = "idempotency_key")
    private UUID idempotencyKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
