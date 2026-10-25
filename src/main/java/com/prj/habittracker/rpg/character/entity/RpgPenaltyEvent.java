package com.prj.habittracker.rpg.character.entity;

import com.prj.habittracker.common.enums.PenaltySourceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "rpg_penalty_events",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_rpg_penalty_source",
                columnNames = {"source_type", "source_id"}
        )
)
@Getter
@Setter
public class RpgPenaltyEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 40)
    private PenaltySourceType sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "penalty_date", nullable = false)
    private LocalDate penaltyDate;

    @Column(name = "hp_amount", nullable = false)
    private Integer hpAmount;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}