package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.HabitScheduleRequest;
import com.prj.habittracker.habit.dto.response.HabitScheduleResponse;
import com.prj.habittracker.habit.service.HabitScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedules")
public class HabitScheduleController {
    private final HabitScheduleService habitScheduleService;

    @PutMapping("/{habitId}")
    public Boolean updateHabitSchedule(@PathVariable("habitId") String habitId, @RequestBody @Valid HabitScheduleRequest request) {
        return habitScheduleService.updateHabitSchedule(UUID.fromString(habitId), request);
    }

    @GetMapping("/{habitId}")
    public HabitScheduleResponse getHabitSchedule(@PathVariable("habitId") String habitId) {
        return habitScheduleService.getHabitSchedule(UUID.fromString(habitId));
    }
}
