package com.prj.habittracker.habit.service;

import com.prj.habittracker.habit.dto.request.BasicHabitInformationRequest;
import com.prj.habittracker.habit.dto.request.CreateHabitWithNameRequest;
import com.prj.habittracker.habit.dto.response.BasicHabitInformationResponse;
import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface HabitService {
    HabitSummaryResponse createHabitWithName(CreateHabitWithNameRequest request);
    List<HabitSummaryResponse> getAllHabits();
    BasicHabitInformationResponse updateBasicHabitInformation(BasicHabitInformationRequest request, UUID habitId);
}
