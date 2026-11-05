package com.prj.habittracker.boss.entity;

import com.prj.habittracker.common.enums.*;
import com.prj.habittracker.party.entity.PartyEntity;
import com.prj.habittracker.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "boss_damage_events", uniqueConstraints =
        @UniqueConstraint(name = "uk_boss_damage_source", columnNames = {"source_type", "source_id"}))
@Getter
@Setter
public class BossDamageEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "battle_id", nullable = false)
    private PartyBossBattleEntity battle;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    private PartyEntity party;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private RewardSourceType sourceType;
    @Column(name = "source_id", nullable = false)
    private UUID sourceId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;
    @Column(name = "base_damage", nullable = false)
    private Integer baseDamage;
    @Column(name = "main_stat_snapshot", nullable = false)
    private Integer mainStatSnapshot;
    @Column(name = "boss_damage_percent_snapshot", nullable = false, precision = 7, scale = 2)
    private BigDecimal bossDamagePercentSnapshot;
    @Column(nullable = false)
    private Integer damage;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BossDamageStatus status;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
    @Column(name = "reversed_at")
    private OffsetDateTime reversedAt;
}
