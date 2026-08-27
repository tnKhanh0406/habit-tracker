package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.category.entity.CategoryEntity;
import com.prj.habittracker.category.repository.CategoryRepository;
import com.prj.habittracker.common.enums.CompletionSource;
import com.prj.habittracker.common.exception.ForbiddenException;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.habit.dto.request.BasicHabitInformationRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.BasicHabitInformationResponse;
import com.prj.habittracker.habit.dto.response.CategoryResponse;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.dto.response.HabitTargetResponse;
import com.prj.habittracker.habit.entity.*;
import com.prj.habittracker.habit.mapper.HabitSummaryMapper;
import com.prj.habittracker.habit.repository.*;
import com.prj.habittracker.habit.service.HabitScheduleService;
import com.prj.habittracker.habit.service.HabitService;
import com.prj.habittracker.habit.service.HabitStreakService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitScheduleRepository habitScheduleRepository;
    private final HabitTargetRepository habitTargetRepository;
    private final CategoryRepository categoryRepository;
    private final HabitCategoryRepository habitCategoryRepository;
    private final HabitSummaryMapper habitSummaryMapper;
    private final HabitScheduleService habitScheduleService;
    private final HabitStreakService habitStreakService;
    private final CurrentUserService currentUserService;
    private final HabitCheckinRepository habitCheckinRepository;

    @Override
    @Transactional
    public HabitSummaryResponse createHabitWithName(CreateHabitWithNameRequest request) {
        HabitEntity habitEntity = new HabitEntity();
        habitEntity.setName(request.name());
        habitEntity.setUser(currentUserService.getCurrentUser());
        HabitEntity saved = habitRepository.save(habitEntity);

        HabitScheduleEntity habitScheduleEntity = new HabitScheduleEntity();
        habitScheduleEntity.setHabit(saved);
        habitScheduleRepository.save(habitScheduleEntity);

        HabitTargetEntity habitTargetEntity = new HabitTargetEntity();
        habitTargetEntity.setHabit(saved);
        habitTargetRepository.save(habitTargetEntity);

        return habitSummaryMapper.toResponse(saved, habitTargetEntity, null);
    }

    @Override
    public List<HabitSummaryResponse> getAllHabits() {
        LocalDate today = LocalDate.now();
        List<HabitSummaryResponse> habits = habitRepository.getAllHabits(currentUserService.getCurrentUser().getId(), today);

        if (habits.isEmpty()) {
            return habits;
        }

        List<UUID> habitIds = habits.stream().map(HabitSummaryResponse::id).toList();

        Map<UUID, HabitScheduleEntity> scheduleByHabitId = habitScheduleRepository
                .findAllByHabitIds(habitIds)
                .stream()
                .collect(Collectors.toMap(
                        s -> s.getHabit().getId(),
                        Function.identity()
                ));

        return habits.stream()
                .map(habit -> {
                    HabitScheduleEntity schedule = scheduleByHabitId.get(habit.id());
                    boolean isScheduled = schedule != null && habitScheduleService.isHabitScheduled(schedule, today);

                    return new HabitSummaryResponse(
                            habit.id(), habit.name(), habit.icon(), habit.color(), habit.status(),
                            habit.targetUnit(), habit.targetValue(),
                            habit.progress() != null ? habit.progress() : BigDecimal.ZERO,
                            habit.completed() != null ? habit.completed() : false,
                            isScheduled,
                            habit.currentStreak()
                    );
                })
                .toList();
    }

    @Override
    public BasicHabitInformationResponse updateBasicHabitInformation(BasicHabitInformationRequest request, UUID habitId) {
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(EntityNotFoundException::new);
        habit.setName(request.name());
        habit.setDescription(request.description());
        habit.setColor(request.color());
        habit.setIcon(request.icon());
        habit.setDifficulty(request.difficulty());

        List<CategoryEntity> categories = categoryRepository.findAllById(request.categoryIds());
        validateCategories(categories, request.categoryIds(), currentUserService.getCurrentUser().getId());

        habitCategoryRepository.deleteAll(habitCategoryRepository.findAllByHabitId(habitId));
        habitCategoryRepository.flush();

        List<HabitCategoryEntity> newLinks = categories.stream()
                .map(category -> {
                    HabitCategoryEntity link = new HabitCategoryEntity();
                    link.setHabit(habit);
                    link.setCategory(category);
                    return link;
                })
                .toList();
        habitCategoryRepository.saveAll(newLinks);

        List<CategoryResponse> categoryResponses = categories.stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName()))
                .toList();

        HabitTargetEntity target = habitTargetRepository.findByHabitId(habitId)
                .orElseThrow(EntityNotFoundException::new);
        BigDecimal oldTargetValue = target.getValue();
        BigDecimal newTargetValue = request.targetValue();

        if (oldTargetValue.compareTo(newTargetValue) != 0) {
            updateTodayCheckinAfterTargetChange(habit, newTargetValue);
        }

        target.setUnit(request.targetUnit());
        target.setValue(request.targetValue());
        habitTargetRepository.save(target);

        return new BasicHabitInformationResponse(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getColor(),
                habit.getIcon(),
                habit.getDifficulty(),
                categoryResponses,
                new HabitTargetResponse(target.getValue(), target.getUnit()),
                habit.getCurrentStreak()
        );
    }


    private void updateTodayCheckinAfterTargetChange(HabitEntity habit, BigDecimal newTargetValue) {
        LocalDate today = LocalDate.now();
        HabitCheckinEntity checkin = habitCheckinRepository.findByHabitIdAndCheckinDate(habit.getId(), today)
                .orElse(null);
        if (checkin != null) {
            boolean oldCompleted = checkin.getCompleted();
            boolean reachNewTarget = checkin.getProgress().compareTo(newTargetValue) >= 0;
            CompletionSource source = checkin.getCompletionSource();
            boolean newCompleted;
            if (source == CompletionSource.MANUAL) {
                newCompleted = true;
            } else if (reachNewTarget) {
                newCompleted = true;
                checkin.setCompletionSource(CompletionSource.PROGRESS);
            } else {
                newCompleted = false;
                checkin.setCompletionSource(null);
            }
            checkin.setCompleted(newCompleted);
            habitStreakService.updateStreaks(habit, oldCompleted, newCompleted);
        }
    }

    private void validateCategories(List<CategoryEntity> found, List<UUID> requestedIds, UUID userId) {
        if (found.size() != requestedIds.size()) {
            throw new EntityNotFoundException("Một hoặc nhiều category không tồn tại");
        }
        boolean allBelongToUser = found.stream()
                .allMatch(c -> c.getUser() == null || c.getUser().getId().equals(userId));
        if (!allBelongToUser) {
            throw new ForbiddenException("Category không thuộc về user này");
        }
    }


}
//Case 1: progress < oldTarget, oldCompleted = true
//After update:
    // progress < newTarget, newCompleted = true, streak unchanged
    // progress >= newTarget, newCompleted = true, streak unchanged

//Case 2: progress < oldTarget, oldCompleted = false
//After update:
    // progress < newTarget, newCompleted = false streak unchanged
    // progress >= newTarget, newCompleted = true, streak++

//Case 3: progress >= oldTarget, oldCompleted = true
//After update:
    // progress < newTarget, newCompleted = false, streak-- (streak >= 0)
    // progress >= newTarget, newCompleted = true, streak unchanged

//Case 4: progress >= oldTarget, oldCompleted = false - invalid case, should not happen