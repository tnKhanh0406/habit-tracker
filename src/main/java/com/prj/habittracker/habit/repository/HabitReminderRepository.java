package com.prj.habittracker.habit.repository;

import com.prj.habittracker.habit.dto.response.HabitReminderResponse;
import com.prj.habittracker.habit.entity.HabitReminderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitReminderRepository extends JpaRepository<HabitReminderEntity, UUID> {
    @Query("""
        SELECT new com.prj.habittracker.habit.dto.response.HabitReminderResponse(
            hr.id,
            hr.reminderTime,
            hr.enabled
        )
        FROM HabitReminderEntity hr
        WHERE hr.habit.id = :habitId
    """)
    List<HabitReminderResponse> findAllByHabitId(UUID habitId);

    List<HabitReminderEntity> findAllByHabit_Id(UUID habitId);
}
