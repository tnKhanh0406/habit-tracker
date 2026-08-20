package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.entity.HabitScheduleEntity;

import java.time.LocalDate;

public interface HabitScheduleService {
    boolean isHabitScheduled(HabitScheduleEntity schedule, LocalDate date);
}
