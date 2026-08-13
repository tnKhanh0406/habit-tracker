package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;

import java.util.List;

public interface HabitService {
    HabitSummaryResponse createHabitWithName(CreateHabitWithNameRequest request);
    List<HabitSummaryResponse> getAllHabits();
}
