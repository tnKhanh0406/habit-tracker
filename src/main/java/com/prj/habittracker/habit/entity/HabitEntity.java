package com.prj.habittracker.habit.entity;

import com.prj.habittracker.common.enums.HabitDifficulty;
import com.prj.habittracker.common.enums.HabitStatus;
import com.prj.habittracker.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 15)
    private String color = "#23A8F2";

    @Column(length = 50)
    private String icon = "brain";

    @Enumerated(EnumType.STRING)
    private HabitDifficulty difficulty = HabitDifficulty.EASY;

    @Enumerated(EnumType.STRING)
    private HabitStatus status = HabitStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
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
