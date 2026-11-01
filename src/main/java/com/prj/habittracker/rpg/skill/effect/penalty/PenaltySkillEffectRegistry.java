package com.prj.habittracker.rpg.skill.effect.penalty;

import com.prj.habittracker.common.enums.SkillEffectType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class PenaltySkillEffectRegistry {
    private final Map<SkillEffectType, PenaltySkillEffectHandler> handlers;

    public PenaltySkillEffectRegistry(List<PenaltySkillEffectHandler> handlers) {
        Map<SkillEffectType, PenaltySkillEffectHandler> mapped = new EnumMap<>(SkillEffectType.class);
        handlers.forEach(handler -> {
            if (mapped.put(handler.supports(), handler) != null) {
                throw new IllegalStateException("Duplicate penalty effect handler for " + handler.supports());
            }
        });
        this.handlers = Map.copyOf(mapped);
    }

    public Optional<PenaltySkillEffectHandler> find(SkillEffectType type) {
        return Optional.ofNullable(handlers.get(type));
    }
}
