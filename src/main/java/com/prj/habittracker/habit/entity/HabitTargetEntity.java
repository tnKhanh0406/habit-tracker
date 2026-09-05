package com.prj.habittracker.habit.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
