package com.prj.habittracker.habit.dto.response;

import com.prj.habittracker.common.enums.ScheduleType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record HabitScheduleResponse(
    UUID id,
    ScheduleType scheduleType,
    Integer intervalValue,
    LocalDate startDate,
    LocalDate endDate,
    List<Integer> weekdays
) {
}
