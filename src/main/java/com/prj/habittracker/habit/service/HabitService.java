package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.dto.request.BasicHabitInformationRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.request.HabitScheduleRequest;
import com.prj.habittracker.habit.dto.response.BasicHabitInformationResponse;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface HabitService {
    BasicHabitInformationResponse getHabitDetail(UUID habitId);
    HabitSummaryResponse createHabitWithName(CreateHabitWithNameRequest request);
    HabitSummaryResponse createHabit(CreateHabitRequest request);
    List<HabitSummaryResponse> getAllHabits();
    BasicHabitInformationResponse updateBasicHabitInformation(BasicHabitInformationRequest request, UUID habitId);
}
