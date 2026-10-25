package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.entity.HabitCheckinEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitCheckinRepository extends JpaRepository<HabitCheckinEntity, UUID> {
    Optional<HabitCheckinEntity> findByHabitIdAndCheckinDate(UUID habitId, LocalDate checkinDate);

    List<HabitCheckinEntity> findAllByHabitIdInAndCheckinDate(List<UUID> habitIds, LocalDate checkinDate);
}
