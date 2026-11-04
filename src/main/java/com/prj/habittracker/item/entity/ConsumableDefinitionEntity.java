package com.prj.habittracker.item.entity;

import com.prj.habittracker.common.enums.ConsumableEffectType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "consumable_definitions")
@Getter
@Setter
public class ConsumableDefinitionEntity {
    @Id
    @Column(name = "item_id")
    private UUID itemId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect_type", nullable = false, length = 30)
    private ConsumableEffectType effectType;

    @Column(name = "effect_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal effectValue;
}
