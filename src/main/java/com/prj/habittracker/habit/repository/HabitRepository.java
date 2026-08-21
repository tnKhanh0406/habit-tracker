package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.entity.HabitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
            c.progress,
            CASE WHEN c.id IS NOT NULL THEN c.completed ELSE false END,
            true
        )
        FROM HabitEntity h
        JOIN HabitTargetEntity t ON h.id = t.habit.id
        LEFT JOIN HabitCheckinEntity c ON h.id = c.habit.id AND c.checkinDate = :date
        WHERE h.user.id  = :userId
    """)
    List<HabitSummaryResponse> getAllHabits(UUID userId, LocalDate date);

    @Query("""
        SELECT h
        FROM HabitEntity h
        JOIN FETCH HabitTargetEntity t ON h.id = t.habit.id
        WHERE h.id = :habitId
    """)
    Optional<HabitEntity> findByIdWithTarget(UUID habitId);
}
