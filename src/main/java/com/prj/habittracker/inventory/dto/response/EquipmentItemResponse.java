package com.prj.habittracker.inventory.dto.response;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.common.enums.EquipmentSlot;

import java.math.BigDecimal;

public record EquipmentItemResponse(
        EquipmentSlot slot,
        CharacterClass requiredClass,
        int requiredLevel,
        int strengthBonus,
        int intelligenceBonus,
        int vitalityBonus,
        int agilityBonus,
        int maxHpBonus,
        int maxMpBonus,
        BigDecimal bossDamagePercent
) {}
