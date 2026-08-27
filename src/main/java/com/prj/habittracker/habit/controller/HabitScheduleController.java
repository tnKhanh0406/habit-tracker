package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.HabitScheduleRequest;
import com.prj.habittracker.habit.service.HabitScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule")
public class HabitScheduleController {
    private final HabitScheduleService habitScheduleService;

    @PutMapping("/{habitId}")
    public ResponseEntity<?> updateHabitSchedule(@PathVariable("habitId") String habitId, @RequestBody @Valid HabitScheduleRequest request) {
        habitScheduleService.updateHabitSchedule(UUID.fromString(habitId), request);
        return ResponseEntity.noContent().build();
    }
}
