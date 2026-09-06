package com.prj.habittracker.todo.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCompletedRequest(
        @NotNull
        Boolean completed
) {
}
