package com.prj.habittracker.boss.service;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;

import java.util.UUID;

public interface BossDamageService {
    BossDamageResult applyCompletionDamage(UUID userId, RewardSourceType sourceType, UUID sourceId,
                                           Difficulty difficulty);
    BossDamageResult reverseCompletionDamage(UUID userId, RewardSourceType sourceType, UUID sourceId);
}
