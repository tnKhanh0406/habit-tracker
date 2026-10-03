package com.prj.habittracker.habit.mapper;

import com.prj.habittracker.habit.dto.response.HabitCheckinResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
import com.prj.habittracker.rpg.character.dto.response.RewardChangeResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HabitCheckinMapper {
    public HabitCheckinResponse toHabitCheckinResponse(UUID habitId, HabitCheckinEntity checkinEntity, HabitTargetEntity targetEntity, Integer currentStreak) {
        return toHabitCheckinResponse(habitId, checkinEntity, targetEntity, currentStreak, null);
    }

    public HabitCheckinResponse toHabitCheckinResponse(UUID habitId, HabitCheckinEntity checkinEntity, HabitTargetEntity targetEntity, Integer currentStreak, RewardChangeResponse reward) {
        return new HabitCheckinResponse(
                habitId,
                checkinEntity.getCheckinDate(),
                checkinEntity.getProgress(),
                checkinEntity.getCompleted(),
                targetEntity != null ? targetEntity.getValue() : null,
                currentStreak,
                reward
        );
    }
}
