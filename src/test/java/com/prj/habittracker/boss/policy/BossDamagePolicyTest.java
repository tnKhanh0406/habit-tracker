package com.prj.habittracker.boss.policy;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.model.EffectiveCharacterStats;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BossDamagePolicyTest {
    private final BossDamagePolicy policy = new BossDamagePolicy();

    @Test
    void appliesDifficultyMainStatAndEquipmentBossBonus() {
        var stats = new EffectiveCharacterStats(10, 2, 3, 4, 100, 50, new BigDecimal("5.00"));
        int mainStat = policy.mainStat(CharacterClass.WARRIOR, stats);

        int damage = policy.calculate(RewardSourceType.HABIT_COMPLETION, Difficulty.HARD,
                mainStat, stats.bossDamagePercent());

        assertThat(mainStat).isEqualTo(10);
        assertThat(damage).isEqualTo(18);
    }

    @Test
    void usesDifferentMainStatForEachClass() {
        var stats = new EffectiveCharacterStats(2, 7, 5, 9, 100, 50, BigDecimal.ZERO);

        assertThat(policy.mainStat(CharacterClass.MAGE, stats)).isEqualTo(7);
        assertThat(policy.mainStat(CharacterClass.RANGER, stats)).isEqualTo(9);
        assertThat(policy.mainStat(CharacterClass.CLERIC, stats)).isEqualTo(5);
    }
}
