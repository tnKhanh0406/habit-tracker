package com.prj.habittracker.habit.controller;

import com.prj.habittracker.habit.dto.request.BasicHabitInformationRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.BasicHabitInformationResponse;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/habits")
public class HabitController {
    private final HabitService habitService;

    @GetMapping("/{habitId}")
    public BasicHabitInformationResponse getHabitDetail(@PathVariable("habitId") String habitId) {
        return habitService.getHabitDetail(UUID.fromString(habitId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitSummaryResponse createHabit(@RequestBody CreateHabitRequest request) {
        return habitService.createHabit(request);
    }

    @PostMapping("/name")
    @ResponseStatus(HttpStatus.CREATED)
    public HabitSummaryResponse createHabitWithName(@RequestBody @Valid CreateHabitWithNameRequest request) {
        return habitService.createHabitWithName(request);
    }

    @GetMapping
    public List<HabitSummaryResponse> getAllHabits() {
        return habitService.getAllHabits();
    }

    @PutMapping("/basic/{habitId}")
    public BasicHabitInformationResponse updateBasicHabitInformation(@PathVariable("habitId") String habitId,
                                                                     @RequestBody @Valid BasicHabitInformationRequest request) {
        return habitService.updateBasicHabitInformation(request, UUID.fromString(habitId));
    }
}
