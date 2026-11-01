package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class InstantSkillEffectRegistry {
    private final Map<SkillEffectType, InstantSkillEffectHandler> handlers;

    public InstantSkillEffectRegistry(List<InstantSkillEffectHandler> handlers) {
        Map<SkillEffectType, InstantSkillEffectHandler> mapped = new EnumMap<>(SkillEffectType.class);
        handlers.forEach(handler -> {
            if (mapped.put(handler.supports(), handler) != null) {
                throw new IllegalStateException("Duplicate instant effect handler for " + handler.supports());
            }
        });
        this.handlers = Map.copyOf(mapped);
    }

    public InstantSkillEffectHandler get(SkillEffectType type) {
        InstantSkillEffectHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalStateException("No instant effect handler for " + type);
        }
        return handler;
    }
}
