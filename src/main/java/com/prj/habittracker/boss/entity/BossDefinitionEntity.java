package com.prj.habittracker.boss.entity;

import com.prj.habittracker.common.enums.BossDifficulty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "boss_definitions")
@Getter
@Setter
public class BossDefinitionEntity {
    @Id
    private UUID id;
    @Column(nullable = false, unique = true, length = 60)
    private String code;
    @Column(nullable = false, length = 100)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BossDifficulty difficulty;
    @Column(name = "sequence_order", nullable = false, unique = true)
    private Integer sequenceOrder;
    @Column(name = "base_hp", nullable = false)
    private Integer baseHp;
    @Column(length = 60)
    private String icon;
    @Column(nullable = false)
    private Boolean enabled;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
