package com.prj.habittracker.habit.dto.request;

import java.util.List;

public record CreateHabitRequest(
        BasicHabitInformationRequest basicHabitInformation,
        HabitScheduleRequest schedule,
        List<HabitReminderRequest> reminders
) {
}
