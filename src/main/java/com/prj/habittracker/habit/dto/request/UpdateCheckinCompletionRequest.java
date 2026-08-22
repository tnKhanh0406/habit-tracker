package com.prj.habittracker.habit.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateCheckinCompletionRequest(
        @NotNull
        Boolean completed
) {
}
