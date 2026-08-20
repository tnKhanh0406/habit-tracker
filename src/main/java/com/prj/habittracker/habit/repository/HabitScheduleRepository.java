package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.entity.HabitScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitScheduleRepository extends JpaRepository<HabitScheduleEntity, UUID> {
    Optional<HabitScheduleEntity> findByHabitId(UUID habitId);
    @Query("""
        SELECT DISTINCT s FROM HabitScheduleEntity s
        LEFT JOIN FETCH s.weekdays
        WHERE s.habit.id IN :habitIds
    """)
    List<HabitScheduleEntity> findAllByHabitIds(List<UUID> habitIds);
}
