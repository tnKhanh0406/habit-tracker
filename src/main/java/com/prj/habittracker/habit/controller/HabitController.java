package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.service.HabitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
