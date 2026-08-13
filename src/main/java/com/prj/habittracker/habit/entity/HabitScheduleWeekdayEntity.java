package com.prj.habittracker.habit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "habit_schedule_weekdays",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_schedule_weekday",
                        columnNames = {"schedule_id", "weekday"}
                )
        }
)
@Getter
@Setter
public class HabitScheduleWeekdayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_schedule_id", nullable = false)
    private HabitScheduleEntity habitSchedule;

    @Column(nullable = false)
    private Integer weekday;
}
