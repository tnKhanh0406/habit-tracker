package com.prj.habittracker.rpg.skill.entity;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.common.enums.SkillTargetType;
import com.prj.habittracker.common.enums.SkillType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "skills")
@Getter
@Setter
public class SkillEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true, length = 60)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "character_class", nullable = false, length = 30)
    private CharacterClass characterClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 20)
    private SkillType skillType;

    @Column(name = "mana_cost", nullable = false)
    private Integer manaCost;

    @Column(name = "required_level", nullable = false)
    private Integer requiredLevel;

    @Column(name = "cooldown_seconds", nullable = false)
    private Integer cooldownSeconds;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 30)
    private SkillTargetType targetType;

    @Column(nullable = false)
    private Boolean enabled;

    @OneToMany(mappedBy = "skill", fetch = FetchType.LAZY)
    private List<SkillEffectEntity> effects = new ArrayList<>();
}
