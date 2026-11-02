package com.prj.habittracker.rpg.skill.dto.response;

import com.prj.habittracker.common.enums.SkillDurationType;
import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.common.enums.SkillValueType;

import java.math.BigDecimal;

public record SkillEffectResponse(
        SkillEffectType type,
        BigDecimal value,
        BigDecimal conditionValue,
        SkillValueType valueType,
        SkillDurationType durationType
) {}
