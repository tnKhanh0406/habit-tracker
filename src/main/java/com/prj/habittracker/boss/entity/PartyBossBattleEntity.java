package com.prj.habittracker.boss.entity;

import com.prj.habittracker.common.enums.BossBattleStatus;
import com.prj.habittracker.party.entity.PartyEntity;
import com.prj.habittracker.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "party_boss_battles", uniqueConstraints =
        @UniqueConstraint(name = "uk_party_boss_once", columnNames = {"party_id", "boss_definition_id"}))
@Getter
@Setter
public class PartyBossBattleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "party_id", nullable = false)
    private PartyEntity party;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boss_definition_id", nullable = false)
    private BossDefinitionEntity bossDefinition;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "started_by_user_id", nullable = false)
    private UserEntity startedBy;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BossBattleStatus status;
    @Column(name = "max_hp", nullable = false)
    private Integer maxHp;
    @Column(name = "current_hp", nullable = false)
    private Integer currentHp;
    @Version
    private Long version;
    @CreationTimestamp
    @Column(name = "started_at", nullable = false, updatable = false)
    private OffsetDateTime startedAt;
    @Column(name = "defeated_at")
    private OffsetDateTime defeatedAt;
}
