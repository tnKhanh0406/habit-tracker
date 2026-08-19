package com.prj.habittracker.habit.dto.request;

import com.prj.habittracker.common.enums.ScheduleType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record HabitScheduleRequest(
        @NotNull
        ScheduleType scheduleType,
        @NotNull
        @Min(1)
        Integer intervalValue,
        @NotNull
        LocalDate startDate,
        LocalDate endDate,
        @NotNull
        List<@Min(0) @Max(6) Integer> weekdays
) {
}
