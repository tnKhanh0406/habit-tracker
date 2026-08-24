package com.prj.habittracker.todo.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TodoSummaryResponse(
        UUID id,
        String title,
        Boolean completed,
        LocalDate dueDate,
        LocalTime dueTime,
        List<TodoSubtaskResponse> subtasks
) {
}
