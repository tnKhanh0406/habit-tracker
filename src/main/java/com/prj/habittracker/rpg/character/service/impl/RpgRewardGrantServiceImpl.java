package com.prj.habittracker.rpg.character.service.impl;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.RewardGrantStatus;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.rpg.character.config.RpgRewardCalculator;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.dto.response.RpgReward;
import com.prj.habittracker.rpg.character.entity.RpgRewardGrant;
import com.prj.habittracker.rpg.character.repository.RpgRewardGrantRepository;
import com.prj.habittracker.rpg.character.service.CharacterProgressionService;
import com.prj.habittracker.rpg.character.service.RpgRewardGrantService;
import com.prj.habittracker.rpg.character.service.RewardOperationResult;
import com.prj.habittracker.rpg.skill.service.SkillEffectService;
import com.prj.habittracker.todo.entity.TodoEntity;
import com.prj.habittracker.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.DateTimeException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RpgRewardGrantServiceImpl implements RpgRewardGrantService {
    private final RpgRewardCalculator rewardCalculator;
    private final RpgRewardGrantRepository rewardGrantRepository;
    private final CharacterProgressionService characterProgressionService;
    private final SkillEffectService skillEffectService;
    private final TodoRepository todoRepository;

    @Override
    @Transactional
    public RewardOperationResult applyHabitCompletion(UUID userId, UUID completionId, Difficulty difficulty, int streak) {
        Optional<RpgRewardGrant> existing = rewardGrantRepository.findBySourceTypeAndSourceId(RewardSourceType.HABIT_COMPLETION, completionId);
        if (existing.isPresent() && existing.get().getStatus() == RewardGrantStatus.APPLIED) {
            return noChange(userId);
        }
        CharacterResponse before = characterProgressionService.getCharacter(userId);
        RpgReward reward = skillEffectService.applyCompletionEffects(
                userId,
                RewardSourceType.HABIT_COMPLETION,
                completionId,
                difficulty,
                streak,
                false,
                0,
                rewardCalculator.calculateHabit(difficulty, streak)
        );
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
        CharacterResponse after = characterProgressionService.applyReward(userId, reward.exp(), reward.coin());
        return new RewardOperationResult(reward.exp(), reward.coin(), before.level(), after);
    }

    @Override
    @Transactional
    public RewardOperationResult applyTodoCompletion(UUID userId, UUID completionId, Difficulty difficulty) {
        Optional<RpgRewardGrant> existing = rewardGrantRepository.findBySourceTypeAndSourceId(RewardSourceType.TODO_COMPLETION, completionId);

        if (existing.isPresent() && existing.get().getStatus() == RewardGrantStatus.APPLIED) {
            return noChange(userId);
        }

        CharacterResponse before = characterProgressionService.getCharacter(userId);
        TodoCompletionContext completionContext = todoCompletionContext(userId, completionId);
        RpgReward reward = skillEffectService.applyCompletionEffects(
                userId,
                RewardSourceType.TODO_COMPLETION,
                completionId,
                difficulty,
                0,
                completionContext.beforeDeadline(),
                completionContext.previousDailyCompletions(),
                rewardCalculator.calculateTodo(difficulty)
        );
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

        CharacterResponse after = characterProgressionService.applyReward(userId, reward.exp(), reward.coin());
        return new RewardOperationResult(reward.exp(), reward.coin(), before.level(), after);
    }

    @Override
    @Transactional
    public RewardOperationResult reverseReward(UUID userId, RewardSourceType sourceType, UUID sourceId) {
        Optional<RpgRewardGrant> existing = rewardGrantRepository.findBySourceTypeAndSourceId(sourceType, sourceId);
        if (existing.isEmpty()) {
            return noChange(userId);
        }
        RpgRewardGrant grant = existing.get();

        if (!grant.getUserId().equals(userId)) {
            throw new IllegalStateException("Reward does not belong to user");
        }

        if (grant.getStatus() == RewardGrantStatus.REVERSED) {
            return noChange(userId);
        }

        CharacterResponse before = characterProgressionService.getCharacter(userId);
        CharacterResponse after = characterProgressionService.reverseReward(userId, grant.getExpAmount(), grant.getCoinAmount());
        grant.setStatus(RewardGrantStatus.REVERSED);
        grant.setReversedAt(OffsetDateTime.now());
        return new RewardOperationResult(-grant.getExpAmount(), -grant.getCoinAmount(), before.level(), after);
    }

    private RewardOperationResult noChange(UUID userId) {
        CharacterResponse character = characterProgressionService.getCharacter(userId);
        return new RewardOperationResult(0, 0, character.level(), character);
    }

    private TodoCompletionContext todoCompletionContext(UUID userId, UUID todoId) {
        TodoEntity todo = todoRepository.findByIdAndUserIdWithSubtasks(todoId, userId).orElse(null);
        if (todo == null || todo.getCompletedAt() == null) {
            return new TodoCompletionContext(false, 0);
        }

        ZoneId zone = resolveZone(todo.getUser().getTimezone());
        OffsetDateTime start = todo.getCompletedAt().atZoneSameInstant(zone).toLocalDate()
                .atStartOfDay(zone).toOffsetDateTime();
        OffsetDateTime end = start.atZoneSameInstant(zone).plusDays(1).toOffsetDateTime();
        long completedToday = todoRepository
                .countByUserIdAndCompletedAtGreaterThanEqualAndCompletedAtLessThan(userId, start, end);

        boolean beforeDeadline = false;
        if (todo.getDueDate() != null) {
            LocalTime dueTime = todo.getDueTime() == null ? LocalTime.MAX : todo.getDueTime();
            OffsetDateTime deadline = todo.getDueDate().atTime(dueTime).atZone(zone).toOffsetDateTime();
            beforeDeadline = !todo.getCompletedAt().isAfter(deadline);
        }
        return new TodoCompletionContext(beforeDeadline, (int) Math.max(0, completedToday - 1));
    }

    private ZoneId resolveZone(String timezone) {
        try {
            return ZoneId.of(timezone);
        } catch (DateTimeException | NullPointerException exception) {
            return ZoneId.systemDefault();
        }
    }

    private record TodoCompletionContext(boolean beforeDeadline, int previousDailyCompletions) {}
}
