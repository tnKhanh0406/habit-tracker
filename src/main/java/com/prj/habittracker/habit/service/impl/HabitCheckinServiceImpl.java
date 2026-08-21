package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.habit.dto.request.AddCheckinProgressRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinCompletionRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinProgressRequest;
import com.prj.habittracker.habit.dto.response.HabitCheckinResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
import com.prj.habittracker.habit.mapper.HabitCheckinMapper;
import com.prj.habittracker.habit.repository.HabitCheckinRepository;
import com.prj.habittracker.habit.repository.HabitRepository;
import com.prj.habittracker.habit.repository.HabitTargetRepository;
import com.prj.habittracker.habit.service.HabitCheckinService;
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
    private final HabitTargetRepository habitTargetRepository;
    private final HabitRepository habitRepository;
    private final HabitCheckinMapper habitCheckinMapper;

    @Transactional
    @Override
    public HabitCheckinResponse addProgress(UUID habitId, AddCheckinProgressRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId)
                .orElseThrow(EntityNotFoundException::new);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = habitCheckinRepository.findByHabitIdAndCheckinDate(habitId, today)
                .orElseGet(() -> createCheckin(habit, today));
        BigDecimal newProgress = checkin.getProgress().add(request.amount());
        checkin.setProgress(newProgress);
        if (newProgress.compareTo(habit.getTarget().getValue()) >= 0) {
            checkin.setCompleted(true);
        }
        return habitCheckinMapper.toHabitCheckinResponse(checkin, habit.getTarget());
    }

    private HabitCheckinEntity createCheckin(HabitEntity habit, LocalDate today) {
        HabitCheckinEntity checkin = new HabitCheckinEntity();
        checkin.setHabit(habit);
        checkin.setCheckinDate(today);
        checkin.setProgress(BigDecimal.ZERO);
        checkin.setCompleted(false);
        return checkin;
    }

    @Transactional
    @Override
    public HabitCheckinResponse updateProgress(UUID habitId, UpdateCheckinProgressRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId)
                        .orElseThrow(EntityNotFoundException::new);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = habitCheckinRepository.findByHabitIdAndCheckinDate(habitId, today)
                .orElseGet(() -> createCheckin(habit, today));
        checkin.setProgress(request.progress());
        checkin.setCompleted(request.progress().compareTo(habit.getTarget().getValue()) >= 0);
        return habitCheckinMapper.toHabitCheckinResponse(checkin, habit.getTarget());
    }

    @Transactional
    @Override
    public HabitCheckinResponse updateCompletion(UUID habitId, UpdateCheckinCompletionRequest request) {
        HabitEntity habit = habitRepository.findByIdWithTarget(habitId)
                .orElseThrow(EntityNotFoundException::new);
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = habitCheckinRepository.findByHabitIdAndCheckinDate(habitId, today)
                .orElseGet(() -> createCheckin(habit, today));
        checkin.setCompleted(request.completed());
        return habitCheckinMapper.toHabitCheckinResponse(checkin, habit.getTarget());
    }
}
