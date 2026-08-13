package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.entity.HabitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface HabitRepository extends JpaRepository<HabitEntity, UUID> {
    @Query("""
        SELECT new com.prj.habittracker.habit.dto.response.HabitSummaryResponse(
            h.id,
            h.name,
            h.icon,
            h.color,
            h.status,
            t.unit,
            t.value,
            c.progress
        )
        FROM HabitEntity h
        JOIN HabitTargetEntity t ON h.id = t.habit.id
        JOIN HabitCheckinEntity c ON h.id = c.habit.id
        WHERE h.user.id  = :userId
    """)
    List<HabitSummaryResponse> getAllHabits(UUID userId);
}
