package com.prj.habittracker.habit.dto.response;

import com.prj.habittracker.common.enums.HabitStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record HabitSummaryResponse(
        UUID id,
        String name,
        String icon,
        String color,
        HabitStatus status,
        String targetUnit,
        BigDecimal targetValue,
        BigDecimal progress
) {
}
