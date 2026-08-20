package com.prj.habittracker.habit.mapper;

import com.prj.habittracker.habit.dto.response.HabitReminderResponse;
import com.prj.habittracker.habit.entity.HabitReminderEntity;
import org.springframework.stereotype.Component;

@Component
public class HabitReminderMapper {
    public HabitReminderResponse toResponse(HabitReminderEntity entity) {
        return new HabitReminderResponse(
                entity.getId(),
                entity.getReminderTime(),
                entity.isEnabled()
        );
    }
}
