package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.dto.response.RewardChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompletionRewardCoordinator {
    private final RpgRewardGrantService rewardGrantService;

    public RewardChangeResponse onCompletionChanged(
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
                return toResponse(rewardGrantService.applyHabitCompletion(userId, sourceId, difficulty, streak));
            } else if (sourceType == RewardSourceType.TODO_COMPLETION) {
                return toResponse(rewardGrantService.applyTodoCompletion(userId, sourceId, difficulty));
            }
        } else if (oldCompleted && !newCompleted) {
            return toResponse(rewardGrantService.reverseReward(userId, sourceType, sourceId));
        }
        return null;
    }

    private RewardChangeResponse toResponse(RewardOperationResult result) {
        return new RewardChangeResponse(
                result.expDelta(),
                result.coinDelta(),
                result.character().exp(),
                result.character().coin(),
                result.character().level(),
                result.character().level() > result.previousLevel(),
                result.character().level() < result.previousLevel()
        );
    }
}