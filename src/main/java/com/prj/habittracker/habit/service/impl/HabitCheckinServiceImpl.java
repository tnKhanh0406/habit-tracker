package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.common.enums.CompletionSource;
import com.prj.habittracker.common.exception.InvalidHabitCheckinException;
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

    @Transactional
    @Override
    public HabitCheckinResponse addProgress(UUID habitId, AddCheckinProgressRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId)
                .orElseThrow(EntityNotFoundException::new);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = getOrCreateTodayCheckin(habit, today);
        BigDecimal newProgress = checkin.getProgress().add(request.amount());
        checkin.setProgress(newProgress);
        boolean oldCompleted = checkin.getCompleted();
        boolean newCompleted = newProgress.compareTo(habit.getTarget().getValue()) >= 0;
        checkin.setCompleted(newCompleted);
        if (newCompleted) {
            checkin.setCompletionSource(CompletionSource.PROGRESS);
        } else {
            checkin.setCompletionSource(null);
        }
        habitStreakService.updateStreaks(habit, oldCompleted, newCompleted);
        return habitCheckinMapper.toHabitCheckinResponse(habitId, checkin, habit.getTarget(), habit.getCurrentStreak());
    }

    @Transactional
    @Override
    public HabitCheckinResponse updateProgress(UUID habitId, UpdateCheckinProgressRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId)
                .orElseThrow(EntityNotFoundException::new);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = getOrCreateTodayCheckin(habit, today);
        boolean oldCompleted = checkin.getCompleted();
        BigDecimal newProgress = request.progress();
        boolean newCompleted = newProgress.compareTo(habit.getTarget().getValue()) >= 0;
        checkin.setProgress(newProgress);
        checkin.setCompleted(newCompleted);
        if (newCompleted) {
            checkin.setCompletionSource(CompletionSource.PROGRESS);
        } else {
            checkin.setCompletionSource(null);
        }
        habitStreakService.updateStreaks(habit, oldCompleted, newCompleted);
        habitCheckinRepository.save(checkin);
        return habitCheckinMapper.toHabitCheckinResponse(habitId, checkin, habit.getTarget(), habit.getCurrentStreak());
    }

    @Transactional
    @Override
    public HabitCheckinResponse updateCompletion(UUID habitId, UpdateCheckinCompletionRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId)
                .orElseThrow(EntityNotFoundException::new);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = getOrCreateTodayCheckin(habit, today);
        boolean oldCompleted = checkin.getCompleted();
        boolean newCompleted = request.completed();
        boolean reachedTarget = checkin.getProgress().compareTo(habit.getTarget().getValue()) >= 0;
        if (reachedTarget && !newCompleted) {
            throw new InvalidHabitCheckinException("Cannot mark habit incomplete when progress has reached target");
        }
        checkin.setCompleted(newCompleted);
        if (newCompleted) {
            checkin.setCompletionSource(CompletionSource.MANUAL);
        } else {
            checkin.setCompletionSource(null);
        }
        habitStreakService.updateStreaks(habit, oldCompleted, newCompleted);
        habitCheckinRepository.save(checkin);
        return habitCheckinMapper.toHabitCheckinResponse(habitId, checkin, habit.getTarget(), habit.getCurrentStreak());
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
