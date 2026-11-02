package com.prj.habittracker.rpg.character.service;

import com.prj.habittracker.common.enums.HabitStatus;
import com.prj.habittracker.common.enums.PenaltySourceType;
import com.prj.habittracker.common.enums.TrackingType;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.dto.response.PenaltySettlementResponse;
import com.prj.habittracker.rpg.character.entity.RpgPenaltyEvent;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.repository.RpgPenaltyEventRepository;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.repository.HabitCheckinRepository;
import com.prj.habittracker.habit.repository.HabitRepository;
import com.prj.habittracker.habit.service.HabitScheduleService;
import com.prj.habittracker.todo.entity.TodoEntity;
import com.prj.habittracker.todo.repository.TodoRepository;
import com.prj.habittracker.user.entity.UserEntity;
import com.prj.habittracker.rpg.skill.service.SkillEffectService;
import com.prj.habittracker.rpg.skill.effect.penalty.PenaltyAdjustment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoginPenaltyService {
    private final HabitRepository habitRepository;
    private final HabitCheckinRepository habitCheckinRepository;
    private final HabitScheduleService habitScheduleService;
    private final TodoRepository todoRepository;
    private final RpgPenaltyEventRepository penaltyEventRepository;
    private final CharacterProgressionService characterProgressionService;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final PlayerCharacterService playerCharacterService;
    private final SkillEffectService skillEffectService;

    @Transactional
    public PenaltySettlementResponse settle(UserEntity user, LocalDate today) {
        UUID userId = user.getId();
        ensureCharacter(userId);

        LocalDate previousLoginDate = user.getLastLoginDate();
        user.setLastLoginDate(today);

        if (previousLoginDate == null || !previousLoginDate.isBefore(today)) {
            return null;
        }

        List<HabitEntity> missedHabitEntities = settleMissedHabits(userId, previousLoginDate);
        int missedHabits = missedHabitEntities.size();
        int overdueTodos = settleOverdueTodos(userId, today, currentTime(user));
        int penaltyCount = missedHabits + overdueTodos;
        if (penaltyCount == 0) {
            return null;
        }

        Set<UUID> missedHabitIds = missedHabitEntities.stream().map(HabitEntity::getId).collect(Collectors.toSet());
        PenaltyAdjustment adjustment = skillEffectService.applyPenaltyEffects(userId, penaltyCount, missedHabitIds);
        missedHabitEntities.stream()
                .filter(habit -> !adjustment.protectedHabitIds().contains(habit.getId()))
                .forEach(habit -> habit.setCurrentStreak(0));
        int finalDamage = adjustment.damage();
        CharacterResponse character = finalDamage > 0
                ? characterProgressionService.applyHpDamage(userId, finalDamage)
                : characterProgressionService.getCharacter(userId);
        return new PenaltySettlementResponse(
                -finalDamage,
                character.hp(),
                missedHabits,
                overdueTodos
        );
    }

    private List<HabitEntity> settleMissedHabits(UUID userId, LocalDate date) {
        List<HabitEntity> missedHabits = new java.util.ArrayList<>();
        List<HabitEntity> habits = habitRepository.findAllForPenalty(userId, HabitStatus.ACTIVE);
        if (habits.isEmpty()) {
            return List.of();
        }

        List<UUID> habitIds = habits.stream().map(HabitEntity::getId).toList();
        Map<UUID, HabitCheckinEntity> checkinsByHabitId = habitCheckinRepository
                .findAllByHabitIdInAndCheckinDate(habitIds, date)
                .stream()
                .collect(Collectors.toMap(checkin -> checkin.getHabit().getId(), Function.identity()));

        Map<UUID, UUID> penaltySourceByHabitId = habits.stream()
                .collect(Collectors.toMap(
                        HabitEntity::getId,
                        habit -> missedHabitSourceId(habit.getId(), date)
                ));
        Set<UUID> existingPenaltySourceIds = Set.copyOf(penaltyEventRepository.findExistingSourceIds(
                PenaltySourceType.HABIT_MISSED_DAY,
                penaltySourceByHabitId.values()
        ));

        for (HabitEntity habit : habits) {
            if (habit.getSchedule() == null || !habitScheduleService.isHabitScheduled(habit.getSchedule(), date)) {
                continue;
            }

            HabitCheckinEntity checkin = checkinsByHabitId.get(habit.getId());
            boolean completed = checkin != null && (
                    Boolean.TRUE.equals(checkin.getCompleted())
                            || (habit.getTrackingType() == TrackingType.QUANTITY
                            && habit.getTarget() != null
                            && checkin.getProgress().compareTo(habit.getTarget().getValue()) >= 0)
            );
            if (completed) {
                continue;
            }

            UUID sourceId = penaltySourceByHabitId.get(habit.getId());
            if (existingPenaltySourceIds.contains(sourceId)) {
                continue;
            }

            savePenaltyEvent(userId, PenaltySourceType.HABIT_MISSED_DAY, sourceId, date);
            missedHabits.add(habit);
        }
        return missedHabits;
    }

    private UUID missedHabitSourceId(UUID habitId, LocalDate date) {
        return UUID.nameUUIDFromBytes((habitId + ":" + date).getBytes(StandardCharsets.UTF_8));
    }

    private int settleOverdueTodos(UUID userId, LocalDate today, LocalTime currentTime) {
        int count = 0;
        List<TodoEntity> todos = todoRepository.findAllByUserIdAndCompletedFalseAndDueDateLessThanEqual(userId, today);
        if (todos.isEmpty()) {
            return 0;
        }
        Set<UUID> existingPenaltySourceIds = Set.copyOf(penaltyEventRepository.findExistingSourceIds(
                PenaltySourceType.TODO_OVERDUE,
                todos.stream().map(TodoEntity::getId).toList()
        ));
        for (TodoEntity todo : todos) {
            boolean overdue = todo.getDueDate().isBefore(today)
                    || (todo.getDueDate().isEqual(today)
                    && todo.getDueTime() != null
                    && todo.getDueTime().isBefore(currentTime));
            if (!overdue) {
                continue;
            }
            if (existingPenaltySourceIds.contains(todo.getId())) {
                continue;
            }
            savePenaltyEvent(userId, PenaltySourceType.TODO_OVERDUE, todo.getId(), today);
            count++;
        }
        return count;
    }

    private void savePenaltyEvent(UUID userId, PenaltySourceType sourceType, UUID sourceId, LocalDate date) {
        RpgPenaltyEvent event = new RpgPenaltyEvent();
        event.setUserId(userId);
        event.setSourceType(sourceType);
        event.setSourceId(sourceId);
        event.setPenaltyDate(date);
        event.setHpAmount(1);
        penaltyEventRepository.save(event);
    }

    private void ensureCharacter(UUID userId) {
        if (!playerCharacterRepository.existsByUserId(userId)) {
            playerCharacterService.createCharacter(userId);
        }
    }

    private LocalTime currentTime(UserEntity user) {
        try {
            return LocalTime.now(ZoneId.of(user.getTimezone()));
        } catch (DateTimeException | NullPointerException exception) {
            return LocalTime.now();
        }
    }
}
