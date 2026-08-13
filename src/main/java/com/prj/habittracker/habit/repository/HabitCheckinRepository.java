package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HabitCheckinRepository extends JpaRepository<HabitCheckinEntity, UUID> {
}
