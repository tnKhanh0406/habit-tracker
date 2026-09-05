package com.prj.habittracker.habit.entity;

import com.prj.habittracker.common.enums.ScheduleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "habit_schedules")
@Getter
@Setter
public class HabitScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private HabitEntity habit;

    @Enumerated(EnumType.STRING)
    private ScheduleType scheduleType = ScheduleType.DAILY;

    private Integer intervalValue = 1;

    @CreationTimestamp
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(
            mappedBy = "habitSchedule",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<HabitScheduleWeekdayEntity> weekdays = new ArrayList<>();
}
