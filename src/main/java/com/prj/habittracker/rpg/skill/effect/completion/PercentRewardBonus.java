package com.prj.habittracker.rpg.skill.effect.completion;

import java.math.BigDecimal;
import java.math.RoundingMode;

final class PercentRewardBonus {
    private PercentRewardBonus() {}

    static long calculate(long base, BigDecimal percent) {
        return BigDecimal.valueOf(base).multiply(percent)
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN).longValue();
    }
}
