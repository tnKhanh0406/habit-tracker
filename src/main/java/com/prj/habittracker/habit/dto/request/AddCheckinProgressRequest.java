package com.prj.habittracker.habit.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddCheckinProgressRequest(
        @NotNull()
        BigDecimal amount
) {
}
