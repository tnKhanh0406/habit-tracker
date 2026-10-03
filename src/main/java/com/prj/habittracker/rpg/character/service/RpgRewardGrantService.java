package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;

import java.util.UUID;

public interface RpgRewardGrantService {
    RewardOperationResult applyHabitCompletion(UUID userId, UUID completionId, Difficulty difficulty, int streak);
    RewardOperationResult applyTodoCompletion(UUID userId, UUID completionId, Difficulty difficulty);
    RewardOperationResult reverseReward(UUID userId, RewardSourceType sourceType, UUID sourceId);
}
