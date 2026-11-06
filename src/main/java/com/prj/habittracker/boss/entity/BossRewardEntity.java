package com.prj.habittracker.boss.entity;

import com.prj.habittracker.common.enums.BossRewardType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "boss_rewards", uniqueConstraints =
        @UniqueConstraint(name = "uk_boss_reward_type", columnNames = {"boss_definition_id", "reward_type"}))
@Getter
@Setter
public class BossRewardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "boss_definition_id", nullable = false)
    private BossDefinitionEntity bossDefinition;
    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false, length = 20)
    private BossRewardType rewardType;
    @Column(nullable = false)
    private Long amount;
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
