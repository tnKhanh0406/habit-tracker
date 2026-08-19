package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.BasicHabitInformationRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.request.HabitScheduleRequest;
import com.prj.habittracker.habit.dto.response.BasicHabitInformationResponse;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits")
public class HabitController {
    private final HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitSummaryResponse> createHabitWithName(@RequestBody @Valid CreateHabitWithNameRequest request) {
        HabitSummaryResponse response = habitService.createHabitWithName(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<HabitSummaryResponse>> getAllHabits() {
        List<HabitSummaryResponse> response = habitService.getAllHabits();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/basic/{habitId}")
    public ResponseEntity<?> updateBasicHabitInformation(@PathVariable("habitId") String habitId, @RequestBody @Valid BasicHabitInformationRequest request) {
        BasicHabitInformationResponse response = habitService.updateBasicHabitInformation(request, UUID.fromString(habitId));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/schedule/{habitId}")
    public ResponseEntity<?> updateHabitSchedule(@PathVariable("habitId") String habitId, @RequestBody @Valid HabitScheduleRequest request) {
        habitService.updateHabitSchedule(UUID.fromString(habitId), request);
        return ResponseEntity.noContent().build();
    }
}
