package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits")
public class HabitController {
    private final HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitSummaryResponse> createHabitWithName(@RequestBody CreateHabitWithNameRequest request) {
        HabitSummaryResponse response = habitService.createHabitWithName(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<HabitSummaryResponse>> getAllHabits() {
        List<HabitSummaryResponse> response = habitService.getAllHabits();
        return ResponseEntity.ok(response);
    }
}
