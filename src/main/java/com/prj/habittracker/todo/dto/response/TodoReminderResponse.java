package com.prj.habittracker.todo.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TodoReminderResponse(
        UUID id,
        LocalDateTime reminderTime,
        Boolean enabled
) {
}
