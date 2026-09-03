package com.prj.habittracker.habit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.UUID;

public record HabitReminderRequest(
        UUID id,
        @NotNull
        LocalTime reminderTime,
        boolean enabled
) {
}
