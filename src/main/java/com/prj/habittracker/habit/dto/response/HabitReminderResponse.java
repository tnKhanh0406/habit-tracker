package com.prj.habittracker.habit.dto.response;

import java.time.LocalTime;
import java.util.UUID;

public record HabitReminderResponse(
        UUID id,
        LocalTime reminderTime,
        Boolean enabled
) {
}
