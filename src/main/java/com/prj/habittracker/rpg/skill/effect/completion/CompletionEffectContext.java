package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.Difficulty;

import java.util.UUID;

public record CompletionEffectContext(
        RewardSourceType sourceType,
        UUID sourceId,
        Difficulty difficulty,
        int streak,
        boolean completedBeforeDeadline,
        int previousDailyCompletions
) {}
