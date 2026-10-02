package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompletionRewardCoordinator {
    private final RpgRewardGrantService rewardGrantService;

    public void onCompletionChanged(
            UUID userId,
            RewardSourceType sourceType,
            UUID sourceId,
            boolean oldCompleted,
            boolean newCompleted,
            Difficulty difficulty,
            int streak
    ) {
        if (!oldCompleted && newCompleted) {
            if (sourceType == RewardSourceType.HABIT_COMPLETION) {
                rewardGrantService.applyHabitCompletion(userId, sourceId, difficulty, streak);
            } else if (sourceType == RewardSourceType.TODO_COMPLETION) {
                rewardGrantService.applyTodoCompletion(userId, sourceId, difficulty);
            }
        } else if (oldCompleted && !newCompleted) {
            rewardGrantService.reverseReward(userId, sourceType, sourceId);
        }
    }
}