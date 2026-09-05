package com.prj.habittracker.habit.entity;

import com.prj.habittracker.common.enums.CompletionSource;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "habit_checkins",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_habit_checkin_date",
                        columnNames = {"habit_id", "checkin_date"}
                )
        }
)
@Getter
@Setter
public class HabitCheckinEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private HabitEntity habit;

    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal progress = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean completed = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "completion_source", length = 20)
    private CompletionSource completionSource;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;
}
