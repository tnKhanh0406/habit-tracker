package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.dto.request.HabitScheduleRequest;
import com.prj.habittracker.habit.dto.response.HabitScheduleResponse;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitScheduleEntity;

import java.time.LocalDate;
import java.util.UUID;

public interface HabitScheduleService {
    void createHabitSchedule(HabitEntity habit, HabitScheduleRequest request);
    boolean updateHabitSchedule(UUID habitId, HabitScheduleRequest request);
    boolean isHabitScheduled(HabitScheduleEntity schedule, LocalDate date);
    HabitScheduleResponse getHabitSchedule(UUID habitId);
}
