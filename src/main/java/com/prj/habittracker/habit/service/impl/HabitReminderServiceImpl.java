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

import java.util.*;
import java.util.stream.Collectors;

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
    public List<HabitReminderResponse> updateHabitReminders(UUID habitId, List<HabitReminderRequest> requests) {
        List<HabitReminderEntity> reminders = habitReminderRepository.findAllByHabit_Id(habitId);
        HabitEntity habit = habitRepository.findById(habitId)
                .orElseThrow(EntityNotFoundException::new);
        Map<UUID, HabitReminderEntity> reminderMap = reminders.stream()
                .collect(Collectors.toMap(HabitReminderEntity::getId, r -> r));
        Set<UUID> requestIds = new HashSet<>();
        List<HabitReminderEntity> result = new ArrayList<>();
        for (HabitReminderRequest request : requests) {
            if (request.id() != null) {
                HabitReminderEntity reminder = reminderMap.get(request.id());
                if (reminder == null) {
                    throw new EntityNotFoundException("Habit reminder not found: " + request.id());
                }
                reminder.setReminderTime(request.reminderTime());
                reminder.setEnabled(request.enabled());
                result.add(reminder);
                requestIds.add(request.id());
            } else {
                HabitReminderEntity newReminder = new HabitReminderEntity();
                newReminder.setHabit(habit);
                newReminder.setReminderTime(request.reminderTime());
                newReminder.setEnabled(request.enabled());
                result.add(newReminder);
            }
        }
        List<HabitReminderEntity> deletedReminders = reminders.stream()
                .filter(reminder -> reminder.getId() != null && !requestIds.contains(reminder.getId()))
                .toList();
        habitReminderRepository.deleteAll(deletedReminders);
        List<HabitReminderEntity> savedReminders = habitReminderRepository.saveAll(result);
        return savedReminders.stream().map(habitReminderMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteHabitReminder(UUID reminderId) {
        habitReminderRepository.deleteById(reminderId);
    }
}
