package com.prj.habittracker.habit.dto.response;

import java.math.BigDecimal;

public record HabitTargetResponse(
        BigDecimal targetValue,
        String targetUnit
) {
}
