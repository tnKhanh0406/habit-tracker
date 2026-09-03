package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.HabitReminderRequest;
import com.prj.habittracker.habit.dto.response.HabitReminderResponse;
import com.prj.habittracker.habit.service.HabitReminderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reminders")
public class HabitReminderController {
    private final HabitReminderService habitReminderService;

    @GetMapping("/{habitId}")
    public List<HabitReminderResponse> getAllHabitReminders(@PathVariable ("habitId") UUID habitId) {
        return habitReminderService.getHabitReminders(habitId);
    }

    @PostMapping("/{habitId}")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitReminderResponse createHabitReminder(@PathVariable ("habitId") UUID habitId, @RequestBody @Valid HabitReminderRequest request) {
        return habitReminderService.createHabitReminder(habitId, request);
    }

    @PutMapping("/{reminderId}")
    public HabitReminderResponse updateHabitReminder(@PathVariable ("reminderId") UUID reminderId, @RequestBody @Valid HabitReminderRequest request) {
        return habitReminderService.updateHabitReminder(reminderId, request);
    }

    @PutMapping("/habit/{habitId}")
    public List<HabitReminderResponse> updateHabitReminders(@PathVariable ("habitId") UUID habitId, @RequestBody @Valid List<HabitReminderRequest> requests) {
        return habitReminderService.updateHabitReminders(habitId, requests);
    }

    @DeleteMapping("/{reminderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabitReminder(@PathVariable ("reminderId") UUID reminderId) {
        habitReminderService.deleteHabitReminder(reminderId);
    }
}
