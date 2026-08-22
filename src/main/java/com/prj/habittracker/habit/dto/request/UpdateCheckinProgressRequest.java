package com.prj.habittracker.habit.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateCheckinProgressRequest(
        @PositiveOrZero
        @NotNull()
        BigDecimal progress
) {
}
