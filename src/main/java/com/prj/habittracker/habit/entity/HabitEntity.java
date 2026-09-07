package com.prj.habittracker.habit.entity;

import com.prj.habittracker.common.enums.Difficulty;
import com.prj.habittracker.common.enums.HabitStatus;
import com.prj.habittracker.common.enums.TrackingType;
import com.prj.habittracker.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "habits")
@Getter
@Setter
public class HabitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 15)
    private String color = "#23A8F2";

    @Column(length = 50)
    private String icon = "brain";

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty = Difficulty.EASY;

    @Enumerated(EnumType.STRING)
    private HabitStatus status = HabitStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "tracking_type", nullable = false, length = 20)
    private TrackingType trackingType = TrackingType.CHECK;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @OneToOne(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private HabitTargetEntity target;

    @OneToOne(mappedBy = "habit", cascade = CascadeType.ALL, orphanRemoval = true)
    private HabitScheduleEntity schedule;

    @Column(nullable = false)
    private Integer currentStreak = 0;

    @Column(nullable = false)
    private Integer longestStreak = 0;
}
