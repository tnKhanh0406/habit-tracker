package com.prj.habittracker.rpg.character.model;

import java.math.BigDecimal;

public record EffectiveCharacterStats(
        int strength,
        int intelligence,
        int vitality,
        int agility,
        int maxHp,
        int maxMp,
        BigDecimal bossDamagePercent
) {}
