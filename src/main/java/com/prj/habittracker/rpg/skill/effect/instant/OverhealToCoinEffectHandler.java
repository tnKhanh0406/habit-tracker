package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OverhealToCoinEffectHandler implements InstantSkillEffectHandler {
    private final CharacterStatService characterStatService;
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.OVERHEAL_TO_COIN;
    }

    @Override
    public void apply(SkillEffectEntity effect, PlayerCharacterEntity character) {
        int healAmount = effect.getValue().intValue();
        int missingHp = Math.max(0, characterStatService.calculate(character).maxHp() - character.getHp());
        int restoredHp = Math.min(missingHp, healAmount);
        int overheal = healAmount - restoredHp;

        character.setHp(character.getHp() + restoredHp);
        character.setCoin(character.getCoin() + overheal);
    }
}
