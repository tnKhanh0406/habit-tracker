package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.AddCheckinProgressRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinCompletionRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinProgressRequest;
import com.prj.habittracker.habit.dto.response.HabitCheckinResponse;
import com.prj.habittracker.habit.service.HabitCheckinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/checkins")
public class HabitCheckinController {
    private final HabitCheckinService habitCheckinService;

    @PostMapping("/{habitId}/progress")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitCheckinResponse addProgress(@PathVariable UUID habitId, @RequestBody @Valid AddCheckinProgressRequest request) {
        return habitCheckinService.addProgress(habitId, request);
    }

    @PutMapping("/{habitId}/progress")
    public HabitCheckinResponse updateProgress(@PathVariable UUID habitId, @RequestBody @Valid UpdateCheckinProgressRequest request) {
        return habitCheckinService.updateProgress(habitId, request);
    }

    @PutMapping("/{habitId}/completion")
    public HabitCheckinResponse updateCompletion(@PathVariable UUID habitId, @RequestBody @Valid UpdateCheckinCompletionRequest request) {
        return habitCheckinService.updateCompletion(habitId, request);
    }



}
