package com.prj.habittracker.inventory.dto.response;

import com.prj.habittracker.common.enums.ConsumableEffectType;

import java.math.BigDecimal;

public record ConsumableItemResponse(ConsumableEffectType effectType, BigDecimal effectValue) {}
