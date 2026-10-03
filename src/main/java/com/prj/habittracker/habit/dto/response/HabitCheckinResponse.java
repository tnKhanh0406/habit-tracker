package com.prj.habittracker.habit.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import com.prj.habittracker.rpg.character.dto.response.RewardChangeResponse;

public record HabitCheckinResponse(
        UUID habitId,
        LocalDate checkinDate,
        BigDecimal progress,
        Boolean completed,
        BigDecimal targetValue,
        Integer currentStreak,
        RewardChangeResponse reward
) {
}
