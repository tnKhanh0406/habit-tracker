package com.prj.habittracker.habit.mapper;

import com.prj.habittracker.habit.dto.response.HabitCheckinResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HabitCheckinMapper {
    public HabitCheckinResponse toHabitCheckinResponse(UUID habitId, HabitCheckinEntity checkinEntity, HabitTargetEntity targetEntity, Integer currentStreak) {
        return new HabitCheckinResponse(
                habitId,
                checkinEntity.getCheckinDate(),
                checkinEntity.getProgress(),
                checkinEntity.getCompleted(),
                targetEntity.getValue(),
                currentStreak
        );
    }
}
