package com.prj.habittracker.habit.mapper;

import com.prj.habittracker.category.dto.response.CategoryResponse;
import com.prj.habittracker.common.enums.TrackingType;
import com.prj.habittracker.habit.dto.response.BasicHabitInformationResponse;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.dto.response.HabitTargetResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
import com.prj.habittracker.rpg.character.dto.response.RewardChangeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HabitMapper {
    public HabitSummaryResponse toSummaryResponse(
            HabitEntity habit,
            HabitTargetEntity target,
            HabitCheckinEntity checkin
    ) {
        return new HabitSummaryResponse(
                habit.getId(),
                habit.getName(),
                habit.getIcon(),
                habit.getColor(),
                habit.getStatus(),
                habit.getTrackingType(),
                target != null ? target.getUnit() : null,
                target != null ? target.getValue() : null,
                checkin != null ? checkin.getProgress() : null,
                checkin != null ? checkin.getCompleted() : null,
                true,
                habit.getCurrentStreak()
        );
    }

    public BasicHabitInformationResponse toDetailResponse(
            HabitEntity habit,
            HabitTargetEntity target,
            List<CategoryResponse> categories
    ) {
        return toDetailResponse(habit, target, categories, null);
    }

    public BasicHabitInformationResponse toDetailResponse(
            HabitEntity habit,
            HabitTargetEntity target,
            List<CategoryResponse> categories,
            RewardChangeResponse reward
    ) {
        return new BasicHabitInformationResponse(
                habit.getId(),
                habit.getName(),
                habit.getDescription(),
                habit.getColor(),
                habit.getIcon(),
                habit.getDifficulty(),
                habit.getTrackingType(),
                categories,
                habit.getTrackingType() == TrackingType.QUANTITY && target != null
                        ? new HabitTargetResponse(target.getValue(), target.getUnit())
                        : null,
                habit.getCurrentStreak(),
                reward
        );
    }
}
