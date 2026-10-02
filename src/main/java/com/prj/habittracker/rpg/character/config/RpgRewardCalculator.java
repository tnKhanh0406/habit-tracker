package com.prj.habittracker.rpg.character.config;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class RpgRewardCalculator {
    public RpgReward calculateTodo(Difficulty difficulty) {
        return switch (difficulty) {
            case TRIVIAL -> new RpgReward(5, 8);
            case EASY -> new RpgReward(7, 10);
            case MEDIUM -> new RpgReward(10, 12);
            case HARD -> new RpgReward(12, 15);
            case EXTREME -> new RpgReward(15, 18);
        };
    }

    public RpgReward calculateHabit(Difficulty difficulty, int streak) {
        RpgReward baseReward = getBaseReward(difficulty);

        BigDecimal multiplier = getStreakMultiplier(streak);

        long finalExp =
                BigDecimal.valueOf(baseReward.exp())
                        .multiply(multiplier)
                        .setScale(
                                0,
                                RoundingMode.DOWN
                        )
                        .longValueExact();

        long finalCoin =
                BigDecimal.valueOf(baseReward.coin())
                        .multiply(multiplier)
                        .setScale(
                                0,
                                RoundingMode.DOWN
                        )
                        .longValueExact();

        return new RpgReward(
                finalExp,
                finalCoin
        );
    }

    private RpgReward getBaseReward(Difficulty difficulty) {
        return switch (difficulty) {
            case TRIVIAL -> new RpgReward(3, 5);
            case EASY -> new RpgReward(5, 8);
            case MEDIUM -> new RpgReward(8, 10);
            case HARD -> new RpgReward(10, 12);
            case EXTREME -> new RpgReward(12, 15);
        };
    }

    private BigDecimal getStreakMultiplier(int streak) {
        if (streak <= 1) {
            return BigDecimal.ONE;
        }

        int bonusDays = streak - 1;

        BigDecimal bonus = BigDecimal.valueOf(bonusDays).multiply(new BigDecimal("0.02"));

        BigDecimal maxBonus = new BigDecimal("0.50");

        if (bonus.compareTo(maxBonus) > 0) {
            bonus = maxBonus;
        }

        return BigDecimal.ONE.add(bonus);
    }
}
