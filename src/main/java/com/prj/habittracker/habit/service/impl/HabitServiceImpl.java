package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.common.exception.ForbiddenException;
import com.prj.habittracker.habit.dto.request.BasicHabitInformationRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.BasicHabitInformationResponse;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.entity.*;
import com.prj.habittracker.habit.mapper.HabitSummaryMapper;
import com.prj.habittracker.habit.repository.*;
import com.prj.habittracker.habit.service.HabitService;
import com.prj.habittracker.user.entity.UserEntity;
import com.prj.habittracker.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitRepository habitRepository;
    private final HabitScheduleRepository habitScheduleRepository;
    private final HabitTargetRepository habitTargetRepository;
    private final HabitCheckinRepository habitCheckinRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final HabitCategoryRepository habitCategoryRepository;
    private final HabitSummaryMapper habitSummaryMapper;

    private UserEntity getCurrentUser() {
        UUID id = UUID.fromString("927b1ae6-62fd-4298-8265-1ded965cb274");
        return userRepository.findById(id).get();
    }

    @Override
    @Transactional
    public HabitSummaryResponse createHabitWithName(CreateHabitWithNameRequest request) {
        HabitEntity habitEntity = new HabitEntity();
        habitEntity.setName(request.name());
        habitEntity.setUser(getCurrentUser());
        HabitEntity saved = habitRepository.save(habitEntity);

        HabitScheduleEntity habitScheduleEntity = new HabitScheduleEntity();
        habitScheduleEntity.setHabit(saved);
        habitScheduleRepository.save(habitScheduleEntity);

        HabitTargetEntity habitTargetEntity = new HabitTargetEntity();
        habitTargetEntity.setHabit(saved);
        habitTargetRepository.save(habitTargetEntity);

        HabitCheckinEntity habitCheckinEntity = new HabitCheckinEntity();
        habitTargetEntity.setHabit(saved);
        habitCheckinRepository.save(habitCheckinEntity);

        return habitSummaryMapper.toResponse(saved, habitTargetEntity, habitCheckinEntity);
    }

    @Override
    public List<HabitSummaryResponse> getAllHabits() {
        return habitRepository.getAllHabits(getCurrentUser().getId(), LocalDate.now());
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
        validateCategories(categories, request.categoryIds(), getCurrentUser().getId());

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
        return new BasicHabitInformationResponse(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getColor(),
                habit.getIcon(),
                habit.getDifficulty(),
                request.categoryIds()
        );
    }

    private void validateCategories(List<CategoryEntity> found, List<UUID> requestedIds, UUID userId) {
        if (found.size() != requestedIds.size()) {
            throw new EntityNotFoundException("Một hoặc nhiều category không tồn tại");
        }
        boolean allBelongToUser = found.stream()
                .allMatch(c -> c.getUser().getId().equals(userId));
        if (!allBelongToUser) {
            throw new ForbiddenException("Category không thuộc về user này");
        }
    }
}
