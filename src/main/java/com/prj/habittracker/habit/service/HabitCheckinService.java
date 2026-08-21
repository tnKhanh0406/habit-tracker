package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.dto.request.AddCheckinProgressRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinCompletionRequest;
import com.prj.habittracker.habit.dto.request.UpdateCheckinProgressRequest;
import com.prj.habittracker.habit.dto.response.HabitCheckinResponse;

import java.util.UUID;

public interface HabitCheckinService {
    HabitCheckinResponse addProgress(UUID habitId, AddCheckinProgressRequest request);
    HabitCheckinResponse updateProgress(UUID habitId, UpdateCheckinProgressRequest request);
    HabitCheckinResponse updateCompletion(UUID habitId, UpdateCheckinCompletionRequest request);
}
