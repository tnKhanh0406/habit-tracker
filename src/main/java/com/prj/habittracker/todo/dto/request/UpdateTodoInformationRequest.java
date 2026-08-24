package com.prj.habittracker.todo.dto.request;

import com.prj.habittracker.common.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public record UpdateTodoInformationRequest(
        @NotBlank
        String title,
        String description,
        @NotNull
        LocalDate dueDate,
        @NotNull
        LocalTime dueTime,
        @NotNull
        Difficulty difficulty,
        @NotNull
        List<UUID> categoryIds,
        @NotNull
        List<SubtaskRequest> subtasks
) {
}
