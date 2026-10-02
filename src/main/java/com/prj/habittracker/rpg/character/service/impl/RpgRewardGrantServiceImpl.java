package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardGrantStatus;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.config.RpgRewardCalculator;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.character.entity.RpgRewardGrant;
import com.prj.habittracker.rpg.character.repository.RpgRewardGrantRepository;
import com.prj.habittracker.rpg.character.service.CharacterProgressionService;
import com.prj.habittracker.rpg.character.service.RpgRewardGrantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RpgRewardGrantServiceImpl implements RpgRewardGrantService {
    private final RpgRewardCalculator rewardCalculator;
    private final RpgRewardGrantRepository rewardGrantRepository;
    private final CharacterProgressionService characterProgressionService;

    @Override
    @Transactional
    public RpgRewardGrant applyHabitCompletion(UUID userId, UUID completionId, Difficulty difficulty, int streak) {
        Optional<RpgRewardGrant> existing = rewardGrantRepository.findBySourceTypeAndSourceId(RewardSourceType.HABIT_COMPLETION, completionId);
        if (existing.isPresent() && existing.get().getStatus() == RewardGrantStatus.APPLIED) {
            return existing.get();
        }
        RpgReward reward = rewardCalculator.calculateHabit(difficulty, streak);
        RpgRewardGrant grant;
        if (existing.isPresent()) {
            grant = existing.get();
            grant.setExpAmount(reward.exp());
            grant.setCoinAmount(reward.coin());
            grant.setStreakSnapshot(streak);
            grant.setStatus(RewardGrantStatus.APPLIED);
            grant.setReversedAt(null);
        } else {
            grant = RpgRewardGrant.builder()
                    .userId(userId)
                    .sourceType(RewardSourceType.HABIT_COMPLETION)
                    .sourceId(completionId)
                    .expAmount(reward.exp())
                    .coinAmount(reward.coin())
                    .streakSnapshot(streak)
                    .status(RewardGrantStatus.APPLIED)
                    .build();
        }
        rewardGrantRepository.save(grant);
        characterProgressionService.applyReward(userId, reward.exp(), reward.coin());
        return grant;
    }

    @Override
    @Transactional
    public RpgRewardGrant applyTodoCompletion(UUID userId, UUID completionId, Difficulty difficulty) {
        Optional<RpgRewardGrant> existing = rewardGrantRepository.findBySourceTypeAndSourceId(RewardSourceType.TODO_COMPLETION, completionId);

        if (existing.isPresent() && existing.get().getStatus() == RewardGrantStatus.APPLIED) {
            return existing.get();
        }

        RpgReward reward = rewardCalculator.calculateTodo(difficulty);
        RpgRewardGrant grant;
        if (existing.isPresent()) {
            grant = existing.get();
            grant.setExpAmount(reward.exp());
            grant.setCoinAmount(reward.coin());
            grant.setStreakSnapshot(null);
            grant.setStatus(RewardGrantStatus.APPLIED);
            grant.setReversedAt(null);
        } else {
            grant = RpgRewardGrant.builder()
                    .userId(userId)
                    .sourceType(RewardSourceType.TODO_COMPLETION)
                    .sourceId(completionId)
                    .expAmount(reward.exp())
                    .coinAmount(reward.coin())
                    .status(RewardGrantStatus.APPLIED)
                    .build();
        }

        rewardGrantRepository.save(grant);

        characterProgressionService.applyReward(userId, reward.exp(), reward.coin());
        return grant;
    }

    @Override
    @Transactional
    public void reverseReward(UUID userId, RewardSourceType sourceType, UUID sourceId) {
        Optional<RpgRewardGrant> existing = rewardGrantRepository.findBySourceTypeAndSourceId(sourceType, sourceId);
        if (existing.isEmpty()) {
            return;
        }
        RpgRewardGrant grant = existing.get();

        if (!grant.getUserId().equals(userId)) {
            throw new IllegalStateException("Reward does not belong to user");
        }

        if (grant.getStatus() == RewardGrantStatus.REVERSED) {
            return;
        }

        characterProgressionService.reverseReward(userId, grant.getExpAmount(), grant.getCoinAmount());
        grant.setStatus(RewardGrantStatus.REVERSED);
        grant.setReversedAt(OffsetDateTime.now());
    }
}
