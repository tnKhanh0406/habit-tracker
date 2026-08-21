package com.prj.habittracker.habit.mapper;

import com.prj.habittracker.habit.dto.response.HabitCheckinResponse;
import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import com.prj.habittracker.habit.entity.HabitTargetEntity;
import org.springframework.stereotype.Component;

@Component
public class HabitCheckinMapper {
    public HabitCheckinResponse toHabitCheckinResponse(HabitCheckinEntity checkinEntity, HabitTargetEntity targetEntity) {
        return new HabitCheckinResponse(
                checkinEntity.getCheckinDate(),
                checkinEntity.getProgress(),
                checkinEntity.getCompleted(),
                targetEntity.getValue()
        );
    }
}
