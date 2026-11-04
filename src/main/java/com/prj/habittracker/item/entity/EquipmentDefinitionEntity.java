package com.prj.habittracker.item.entity;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.common.enums.EquipmentSlot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "equipment_definitions")
@Getter
@Setter
public class EquipmentDefinitionEntity {
    @Id
    @Column(name = "item_id")
    private UUID itemId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EquipmentSlot slot;

    @Enumerated(EnumType.STRING)
    @Column(name = "required_class", length = 30)
    private CharacterClass requiredClass;

    @Column(name = "required_level", nullable = false)
    private Integer requiredLevel;

    @Column(name = "strength_bonus", nullable = false)
    private Integer strengthBonus;
    @Column(name = "intelligence_bonus", nullable = false)
    private Integer intelligenceBonus;
    @Column(name = "vitality_bonus", nullable = false)
    private Integer vitalityBonus;
    @Column(name = "agility_bonus", nullable = false)
    private Integer agilityBonus;
    @Column(name = "max_hp_bonus", nullable = false)
    private Integer maxHpBonus;
    @Column(name = "max_mp_bonus", nullable = false)
    private Integer maxMpBonus;
    @Column(name = "boss_damage_percent", nullable = false, precision = 5, scale = 2)
    private BigDecimal bossDamagePercent;
}
