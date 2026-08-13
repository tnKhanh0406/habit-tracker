package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.entity.HabitTargetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HabitTargetRepository extends JpaRepository<HabitTargetEntity, UUID> {
}
