package com.prj.habittracker.habit.dto.response;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.TrackingType;

import java.util.List;
import java.util.UUID;

public record BasicHabitInformationResponse(
        UUID id,
        String name,
        String description,
        String color,
        String icon,
        Difficulty difficulty,
        TrackingType trackingType,
        List<CategoryResponse> categories,
        HabitTargetResponse target,
        Integer streaks
) {}
