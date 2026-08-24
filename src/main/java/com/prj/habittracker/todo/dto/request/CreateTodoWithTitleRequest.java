package com.prj.habittracker.todo.dto.request;

import jakarta.validation.constraints.NotNull;

public record CreateTodoWithTitleRequest(
        @NotNull
        String title
) {
}
