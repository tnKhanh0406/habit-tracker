package com.prj.habittracker.boss.entity;

import com.prj.habittracker.party.entity.PartyEntity;
import com.prj.habittracker.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "boss_reward_grants", uniqueConstraints =
        @UniqueConstraint(name = "uk_boss_reward_grant", columnNames = {"battle_id", "user_id"}))
@Getter
@Setter
public class BossRewardGrantEntity {
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
    @Column(name = "exp_amount", nullable = false)
    private Long expAmount;
    @Column(name = "coin_amount", nullable = false)
    private Long coinAmount;
    @Column(name = "gem_amount", nullable = false)
    private Long gemAmount;
    @Column(name = "damage_contribution", nullable = false)
    private Long damageContribution;
    @CreationTimestamp
    @Column(name = "granted_at", nullable = false, updatable = false)
    private OffsetDateTime grantedAt;
}
