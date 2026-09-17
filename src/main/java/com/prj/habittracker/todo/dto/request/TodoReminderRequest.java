package com.prj.habittracker.todo.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public record TodoReminderRequest(
        UUID id,
        @NotNull
        LocalDateTime reminderTime,
        boolean enabled
) {
}
