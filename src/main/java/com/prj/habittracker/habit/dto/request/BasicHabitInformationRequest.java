package com.prj.habittracker.habit.dto.request;

import com.prj.habittracker.common.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record BasicHabitInformationRequest(
    @NotBlank
    @Size(max = 100)
    String name,
    String description,
    @NotBlank
    String color,
    @NotBlank
    String icon,
    @NotNull
    Difficulty difficulty,
    @NotNull
    List<UUID> categoryIds,
    @NotBlank
    String targetUnit,
    @NotNull
    BigDecimal targetValue
) {
}
