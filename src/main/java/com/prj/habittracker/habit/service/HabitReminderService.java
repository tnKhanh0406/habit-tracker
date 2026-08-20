package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.dto.request.HabitReminderRequest;
import com.prj.habittracker.habit.dto.response.HabitReminderResponse;

import java.util.List;
import java.util.UUID;

public interface HabitReminderService {
    List<HabitReminderResponse> getHabitReminders(UUID habitId);
    HabitReminderResponse createHabitReminder(UUID habitId, HabitReminderRequest request);
    HabitReminderResponse updateHabitReminder(UUID reminderId, HabitReminderRequest request);
    void deleteHabitReminder(UUID reminderId);
}
