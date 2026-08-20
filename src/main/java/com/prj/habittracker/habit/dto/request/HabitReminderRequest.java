package com.prj.habittracker.habit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record HabitReminderRequest(
        @NotNull
        LocalTime reminderTime,
        boolean enabled
) {
}
