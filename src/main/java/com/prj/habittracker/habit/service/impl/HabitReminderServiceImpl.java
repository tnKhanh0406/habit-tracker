package com.prj.habittracker.habit.service.impl;

import com.prj.habittracker.habit.dto.request.HabitReminderRequest;
import com.prj.habittracker.habit.dto.response.HabitReminderResponse;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.habit.entity.HabitReminderEntity;
import com.prj.habittracker.habit.mapper.HabitReminderMapper;
import com.prj.habittracker.habit.repository.HabitReminderRepository;
import com.prj.habittracker.habit.repository.HabitRepository;
import com.prj.habittracker.habit.service.HabitReminderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HabitReminderServiceImpl implements HabitReminderService {

    private final HabitReminderRepository habitReminderRepository;
    private final HabitRepository habitRepository;
    private final HabitReminderMapper habitReminderMapper;

    @Override
    public List<HabitReminderResponse> getHabitReminders(UUID habitId) {
        return habitReminderRepository.findAllByHabitId(habitId);
    }

    @Override
    public HabitReminderResponse createHabitReminder(UUID habitId, HabitReminderRequest request) {
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(EntityNotFoundException::new);
        HabitReminderEntity reminder = new HabitReminderEntity();
        reminder.setHabit(habit);
        reminder.setReminderTime(request.reminderTime());
        reminder.setEnabled(request.enabled());
        HabitReminderEntity saved = habitReminderRepository.save(reminder);
        return habitReminderMapper.toResponse(saved);
    }

    @Override
    public HabitReminderResponse updateHabitReminder(UUID reminderId, HabitReminderRequest request) {
        HabitReminderEntity reminder = habitReminderRepository.findById(reminderId)
                .orElseThrow(EntityNotFoundException::new);
        reminder.setReminderTime(request.reminderTime());
        reminder.setEnabled(request.enabled());
        HabitReminderEntity updated = habitReminderRepository.save(reminder);
        return habitReminderMapper.toResponse(updated);
    }

    @Override
    public void deleteHabitReminder(UUID reminderId) {
        habitReminderRepository.deleteById(reminderId);
    }
}
