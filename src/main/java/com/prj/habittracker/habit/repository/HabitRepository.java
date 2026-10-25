package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.dto.response.HabitSummaryResponse;
import com.prj.habittracker.habit.entity.HabitEntity;
import com.prj.habittracker.common.enums.HabitStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitRepository extends JpaRepository<HabitEntity, UUID> {
    List<HabitEntity> findAllByUserIdAndStatus(UUID userId, HabitStatus status);

    @Query("""
        SELECT DISTINCT h
        FROM HabitEntity h
        LEFT JOIN FETCH h.schedule s
        LEFT JOIN FETCH s.weekdays
        LEFT JOIN FETCH h.target
        WHERE h.user.id = :userId
        AND h.status = :status
    """)
    List<HabitEntity> findAllForPenalty(@Param("userId") UUID userId,
                                        @Param("status") HabitStatus status);

    @Query("""
        SELECT new com.prj.habittracker.habit.dto.response.HabitSummaryResponse(
            h.id,
            h.name,
            h.icon,
            h.color,
            h.status,
            h.trackingType,
            CASE WHEN h.trackingType = com.prj.habittracker.common.enums.TrackingType.QUANTITY THEN t.unit ELSE null END,
            CASE WHEN h.trackingType = com.prj.habittracker.common.enums.TrackingType.QUANTITY THEN t.value ELSE null END,
            c.progress,
            CASE WHEN c.id IS NOT NULL THEN c.completed ELSE false END,
            true,
            h.currentStreak
        )
        FROM HabitEntity h
        LEFT JOIN HabitTargetEntity t ON h.id = t.habit.id
        LEFT JOIN HabitCheckinEntity c ON h.id = c.habit.id AND c.checkinDate = :date
        WHERE h.user.id  = :userId
    """)
    List<HabitSummaryResponse> getAllHabits(UUID userId, LocalDate date);

    @Query("""
        SELECT h
        FROM HabitEntity h
        LEFT JOIN FETCH h.target t
        JOIN FETCH h.schedule s
        WHERE h.id = :habitId
        AND h.user.id = :userId
    """)
    Optional<HabitEntity> findByIdWithTarget(UUID habitId, UUID userId);

    @Modifying
    @Query("""
        delete from HabitEntity h
        where h.id = :habitId
        and h.user.id = :userId
    """)
    int deleteByIdAndUserId(@Param("habitId") UUID habitId, @Param("userId") UUID userId);
}
