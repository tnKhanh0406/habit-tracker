package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.entity.RpgRewardGrant;

import java.util.UUID;

public interface RpgRewardGrantService {
    RpgRewardGrant applyHabitCompletion(UUID userId, UUID completionId, Difficulty difficulty, int streak);
    RpgRewardGrant applyTodoCompletion(UUID userId, UUID completionId, Difficulty difficulty);
    public void reverseReward(UUID userId, RewardSourceType sourceType, UUID sourceId);
}
