package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitScheduleEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
import com.prj.habittracker.habit.mapper.HabitSummaryMapper;
import com.prj.habittracker.habit.repository.HabitCheckinRepository;
import com.prj.habittracker.habit.repository.HabitRepository;
import com.prj.habittracker.habit.repository.HabitScheduleRepository;
import com.prj.habittracker.habit.repository.HabitTargetRepository;
import com.prj.habittracker.habit.service.HabitService;
import com.prj.habittracker.user.entity.UserEntity;
import com.prj.habittracker.user.repository.UserRepository;
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
}
