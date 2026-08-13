package com.prj.habittracker.habit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "habit_targets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_habit_target",
                        columnNames = "habit_id"
                )
        }
)
@Getter
@Setter
public class HabitTargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false, unique = true)
    private HabitEntity habit;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value = BigDecimal.ONE;

    @Column(nullable = false, length = 50)
    private String unit = "done";

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
