package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.common.enums.CompletionSource;
import com.prj.habittracker.common.enums.RewardSourceType;
import com.prj.habittracker.common.enums.TrackingType;
import com.prj.habittracker.common.exception.InvalidHabitCheckinException;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.habit.dto.request.AddCheckinProgressRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinCompletionRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinProgressRequest;
import com.prj.habittracker.habit.dto.response.HabitCheckinResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.mapper.HabitCheckinMapper;
import com.prj.habittracker.habit.repository.HabitCheckinRepository;
import com.prj.habittracker.habit.repository.HabitRepository;
import com.prj.habittracker.habit.service.HabitCheckinService;
import com.prj.habittracker.habit.service.HabitStreakService;
import com.prj.habittracker.habit.service.HabitTrackingDomainService;
import com.prj.habittracker.rpg.character.service.CompletionRewardCoordinator;
import com.prj.habittracker.rpg.character.dto.response.RewardChangeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HabitCheckinServiceImpl implements HabitCheckinService {

    private final HabitCheckinRepository habitCheckinRepository;
    private final HabitRepository habitRepository;
    private final HabitCheckinMapper habitCheckinMapper;
    private final HabitStreakService habitStreakService;
    private final HabitTrackingDomainService habitTrackingDomainService;
    private final CurrentUserService currentUserService;
    private final CompletionRewardCoordinator completionRewardCoordinator;

    @Transactional
    @Override
    public HabitCheckinResponse addProgress(UUID habitId, AddCheckinProgressRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId, currentUserService.getCurrentUserId())
                .orElseThrow(EntityNotFoundException::new);
        habitTrackingDomainService.requireTrackingType(habit, TrackingType.QUANTITY);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = getOrCreateTodayCheckin(habit, today);
        BigDecimal newProgress = checkin.getProgress().add(request.amount());
        checkin.setProgress(newProgress);
        boolean oldCompleted = checkin.getCompleted();
        habitTrackingDomainService.applyProgressCompletion(habit, habit.getTarget(), checkin);
        boolean newCompleted = checkin.getCompleted();
        habitStreakService.updateStreaks(habit, oldCompleted, newCompleted);
        RewardChangeResponse reward = completionRewardCoordinator.onCompletionChanged(
            currentUserService.getCurrentUserId(),
            RewardSourceType.HABIT_COMPLETION,
            checkin.getId(),
            oldCompleted,
            newCompleted,
            habit.getDifficulty(),
            habit.getCurrentStreak()
        );
        return habitCheckinMapper.toHabitCheckinResponse(habitId, checkin, habit.getTarget(), habit.getCurrentStreak(), reward);
    }

    @Transactional
    @Override
    public HabitCheckinResponse updateProgress(UUID habitId, UpdateCheckinProgressRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId, currentUserService.getCurrentUserId())
                .orElseThrow(EntityNotFoundException::new);
        habitTrackingDomainService.requireTrackingType(habit, TrackingType.QUANTITY);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = getOrCreateTodayCheckin(habit, today);
        boolean oldCompleted = checkin.getCompleted();
        BigDecimal newProgress = request.progress();
        checkin.setProgress(newProgress);
        habitTrackingDomainService.applyProgressCompletion(habit, habit.getTarget(), checkin);
        boolean newCompleted = checkin.getCompleted();
        habitStreakService.updateStreaks(habit, oldCompleted, newCompleted);
        RewardChangeResponse reward = completionRewardCoordinator.onCompletionChanged(
            currentUserService.getCurrentUserId(),
            RewardSourceType.HABIT_COMPLETION,
            checkin.getId(),
            oldCompleted,
            newCompleted,
            habit.getDifficulty(),
            habit.getCurrentStreak()
        );
        habitCheckinRepository.save(checkin);
        return habitCheckinMapper.toHabitCheckinResponse(habitId, checkin, habit.getTarget(), habit.getCurrentStreak(), reward);
    }

    @Transactional
    @Override
    public HabitCheckinResponse updateCompletion(UUID habitId, UpdateCheckinCompletionRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId, currentUserService.getCurrentUserId())
                .orElseThrow(EntityNotFoundException::new);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = getOrCreateTodayCheckin(habit, today);
        boolean oldCompleted = checkin.getCompleted();
        boolean newCompleted = request.completed();
        if (habit.getTrackingType() == TrackingType.QUANTITY
            && habitTrackingDomainService.reachedTarget(habit.getTrackingType(), habit.getTarget(), checkin.getProgress())
            && !newCompleted) {
            throw new InvalidHabitCheckinException("Cannot mark habit incomplete when progress has reached target");
        }
        checkin.setCompleted(newCompleted);
        if (newCompleted) {
            checkin.setCompletionSource(CompletionSource.MANUAL);
        } else {
            checkin.setCompletionSource(null);
        }
        habitStreakService.updateStreaks(habit, oldCompleted, newCompleted);
        RewardChangeResponse reward = completionRewardCoordinator.onCompletionChanged(
            currentUserService.getCurrentUserId(),
            RewardSourceType.HABIT_COMPLETION,
            checkin.getId(),
            oldCompleted,
            newCompleted,
            habit.getDifficulty(),
            habit.getCurrentStreak()
        );
        habitCheckinRepository.save(checkin);
        return habitCheckinMapper.toHabitCheckinResponse(
            habitId,
            checkin,
            habit.getTrackingType() == TrackingType.QUANTITY ? habit.getTarget() : null,
            habit.getCurrentStreak(),
            reward
        );
    }

    private HabitCheckinEntity getOrCreateTodayCheckin(HabitEntity habit, LocalDate today) {
        return habitCheckinRepository.findByHabitIdAndCheckinDate(habit.getId(), today)
                .orElseGet(() -> habitCheckinRepository.save(createCheckin(habit, today)));
    }

    private HabitCheckinEntity createCheckin(HabitEntity habit, LocalDate today) {
        HabitCheckinEntity checkin = new HabitCheckinEntity();
        checkin.setHabit(habit);
        checkin.setCheckinDate(today);
        checkin.setProgress(BigDecimal.ZERO);
        checkin.setCompleted(false);
        checkin.setCompletionSource(null);
        return checkin;
    }
}
