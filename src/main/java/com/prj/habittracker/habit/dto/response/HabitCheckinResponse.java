package com.prj.habittracker.habit.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record HabitCheckinResponse(
        UUID habitId,
        LocalDate checkinDate,
        BigDecimal progress,
        Boolean completed,
        BigDecimal targetValue,
        Integer currentStreak
) {
}
