package com.prj.habittracker.todo.dto.response;

import com.prj.habittracker.category.dto.response.CategoryResponse;
import com.prj.habittracker.common.enums.Difficulty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record TodoInformationResponse(
        UUID id,
        String title,
        String description,
        Difficulty difficulty,
        Boolean completed,
        LocalDate dueDate,
        LocalTime dueTime,
        List<CategoryResponse> categories,
        List<TodoSubtaskResponse> subtasks
) {
}
