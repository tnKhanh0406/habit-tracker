package com.prj.habittracker.habit.dto.response;

import com.prj.habittracker.common.enums.Difficulty;

import java.util.List;
import java.util.UUID;

public record BasicHabitInformationResponse(
        UUID id,
        String name,
        String description,
        String color,
        String icon,
        Difficulty difficulty,
        List<CategoryResponse> categories,
        HabitTargetResponse target
) {}
