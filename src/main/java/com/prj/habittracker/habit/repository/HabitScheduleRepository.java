package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.entity.HabitScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HabitScheduleRepository extends JpaRepository<HabitScheduleEntity, UUID> {
}
