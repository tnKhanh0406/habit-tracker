package com.prj.habittracker.boss.policy;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.model.EffectiveCharacterStats;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BossDamagePolicy {
    public int baseDamage(RewardSourceType sourceType) {
        return sourceType == RewardSourceType.HABIT_COMPLETION ? 10 : 12;
    }

    public int mainStat(CharacterClass characterClass, EffectiveCharacterStats stats) {
        if (characterClass == null) return 1;
        return switch (characterClass) {
            case WARRIOR -> stats.strength();
            case MAGE -> stats.intelligence();
            case RANGER -> stats.agility();
            case CLERIC -> stats.vitality();
        };
    }

    public int calculate(RewardSourceType sourceType, Difficulty difficulty, int mainStat,
                         BigDecimal bossDamagePercent) {
        BigDecimal difficultyMultiplier = switch (difficulty) {
            case TRIVIAL -> new BigDecimal("0.75");
            case EASY -> BigDecimal.ONE;
            case MEDIUM -> new BigDecimal("1.25");
            case HARD -> new BigDecimal("1.50");
            case EXTREME -> new BigDecimal("2.00");
        };
        BigDecimal statMultiplier = BigDecimal.ONE.add(new BigDecimal("0.02")
                .multiply(BigDecimal.valueOf(Math.max(0, mainStat))));
        BigDecimal bossMultiplier = BigDecimal.ONE.add(bossDamagePercent
                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        return Math.max(1, BigDecimal.valueOf(baseDamage(sourceType))
                .multiply(difficultyMultiplier)
                .multiply(statMultiplier)
                .multiply(bossMultiplier)
                .setScale(0, RoundingMode.FLOOR)
                .intValue());
    }
}
