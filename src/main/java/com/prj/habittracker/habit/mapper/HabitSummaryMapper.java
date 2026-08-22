package com.prj.habittracker.habit.mapper;

import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
import org.springframework.stereotype.Component;

@Component
public class HabitSummaryMapper {
    public HabitSummaryResponse toResponse(
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
                target.getUnit(),
                target.getValue(),
                checkin != null ? checkin.getProgress() : null,
                checkin != null ? checkin.getCompleted() : null,
                true,
                habit.getCurrentStreak()
        );
    }
}
