package com.prj.habittracker.inventory.effect;

import com.prj.habittracker.common.enums.ConsumableEffectType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ConsumableEffectRegistry {
    private final Map<ConsumableEffectType, ConsumableEffectHandler> handlers;

    public ConsumableEffectRegistry(List<ConsumableEffectHandler> handlers) {
        Map<ConsumableEffectType, ConsumableEffectHandler> mapped = new EnumMap<>(ConsumableEffectType.class);
        handlers.forEach(handler -> {
            if (mapped.put(handler.supports(), handler) != null) {
                throw new IllegalStateException("Duplicate consumable handler for " + handler.supports());
            }
        });
        this.handlers = Map.copyOf(mapped);
    }

    public ConsumableEffectHandler get(ConsumableEffectType type) {
        ConsumableEffectHandler handler = handlers.get(type);
        if (handler == null) throw new IllegalStateException("No consumable handler for " + type);
        return handler;
    }
}
