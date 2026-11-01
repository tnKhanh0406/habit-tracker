package com.prj.habittracker.rpg.skill.effect.completion;

import com.prj.habittracker.common.enums.SkillEffectType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CompletionSkillEffectRegistry {
    private final Map<SkillEffectType, CompletionSkillEffectHandler> handlers;

    public CompletionSkillEffectRegistry(List<CompletionSkillEffectHandler> handlers) {
        Map<SkillEffectType, CompletionSkillEffectHandler> mapped = new EnumMap<>(SkillEffectType.class);
        handlers.forEach(handler -> {
            if (mapped.put(handler.supports(), handler) != null) {
                throw new IllegalStateException("Duplicate completion effect handler for " + handler.supports());
            }
        });
        this.handlers = Map.copyOf(mapped);
    }

    public Optional<CompletionSkillEffectHandler> find(SkillEffectType type) {
        return Optional.ofNullable(handlers.get(type));
    }
}
