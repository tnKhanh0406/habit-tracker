package com.prj.habittracker.item.entity;

import com.prj.habittracker.common.enums.ItemRarity;
import com.prj.habittracker.common.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "items")
@Getter
@Setter
public class ItemEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false, length = 20)
    private ItemType itemType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ItemRarity rarity;

    @Column(nullable = false)
    private Boolean stackable;

    @Column(name = "max_stack", nullable = false)
    private Integer maxStack;

    @Column(length = 80)
    private String icon;

    @Column(nullable = false)
    private Boolean enabled;

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private EquipmentDefinitionEntity equipmentDefinition;

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY)
    private ConsumableDefinitionEntity consumableDefinition;
}
