package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.dto.response.RewardChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.prj.habittracker.boss.service.BossDamageService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompletionRewardCoordinator {
    private final RpgRewardGrantService rewardGrantService;
    private final BossDamageService bossDamageService;

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
                RewardChangeResponse response = toResponse(
                        rewardGrantService.applyHabitCompletion(userId, sourceId, difficulty, streak));
                bossDamageService.applyCompletionDamage(userId, sourceType, sourceId, difficulty);
                return response;
            } else if (sourceType == RewardSourceType.TODO_COMPLETION) {
                RewardChangeResponse response = toResponse(
                        rewardGrantService.applyTodoCompletion(userId, sourceId, difficulty));
                bossDamageService.applyCompletionDamage(userId, sourceType, sourceId, difficulty);
                return response;
            }
        } else if (oldCompleted && !newCompleted) {
            RewardChangeResponse response = toResponse(rewardGrantService.reverseReward(userId, sourceType, sourceId));
            bossDamageService.reverseCompletionDamage(userId, sourceType, sourceId);
            return response;
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
