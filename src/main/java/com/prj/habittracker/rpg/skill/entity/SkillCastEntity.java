package com.prj.habittracker.rpg.skill.entity;

import com.prj.habittracker.common.enums.SkillCastStatus;
import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "skill_casts")
@Getter
@Setter
public class SkillCastEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "character_id", nullable = false)
    private PlayerCharacterEntity character;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    private SkillTargetType targetType;

    @Column(name = "target_id")
    private UUID targetId;

    @Column(name = "mana_spent", nullable = false)
    private Integer manaSpent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SkillCastStatus status;

    @Column(name = "cast_at", nullable = false)
    private OffsetDateTime castAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "consumed_at")
    private OffsetDateTime consumedAt;

    @Column(name = "idempotency_key", nullable = false)
    private UUID idempotencyKey;

    @Version
    private Long version;
}
