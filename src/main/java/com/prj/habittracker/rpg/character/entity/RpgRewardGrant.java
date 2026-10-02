package com.prj.habittracker.rpg.character.entity;

import com.prj.habittracker.common.enums.RewardGrantStatus;
import com.prj.habittracker.common.enums.RewardSourceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "rpg_reward_grants",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rpg_reward_source",
                        columnNames = {
                                "source_type",
                                "source_id"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpgRewardGrant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private RewardSourceType sourceType;

    @Column(name = "source_id", nullable = false)
    private UUID sourceId;

    @Column(name = "exp_amount", nullable = false)
    private Long expAmount;

    @Column(name = "coin_amount", nullable = false)
    private Long coinAmount;

    @Column(name = "streak_snapshot")
    private Integer streakSnapshot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RewardGrantStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "reversed_at")
    private OffsetDateTime reversedAt;

    @Version
    private Long version;

}