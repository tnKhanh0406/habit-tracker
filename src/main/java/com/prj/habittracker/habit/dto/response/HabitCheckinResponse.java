package com.prj.habittracker.habit.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HabitCheckinResponse(
        LocalDate checkinDate,
        BigDecimal progress,
        Boolean completed,
        BigDecimal targetValue
) {
}
