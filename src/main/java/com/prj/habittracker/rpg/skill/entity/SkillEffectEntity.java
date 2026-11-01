package com.prj.habittracker.rpg.skill.entity;

import com.prj.habittracker.common.enums.SkillDurationType;
import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.common.enums.SkillValueType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "skill_effects")
@Getter
@Setter
public class SkillEffectEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "skill_id", nullable = false)
    private SkillEntity skill;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect_type", nullable = false, length = 40)
    private SkillEffectType effectType;

    @Column(name = "effect_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "value_type", nullable = false, length = 20)
    private SkillValueType valueType;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_type", nullable = false, length = 30)
    private SkillDurationType durationType;
}
