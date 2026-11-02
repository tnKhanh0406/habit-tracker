package com.prj.habittracker.rpg.skill.effect.instant;

import com.prj.habittracker.common.enums.SkillEffectType;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.skill.entity.SkillEffectEntity;
import org.springframework.stereotype.Component;

@Component
public class OverhealToCoinEffectHandler implements InstantSkillEffectHandler {
    @Override
    public SkillEffectType supports() {
        return SkillEffectType.OVERHEAL_TO_COIN;
    }

    @Override
    public void apply(SkillEffectEntity effect, PlayerCharacterEntity character) {
        int healAmount = effect.getValue().intValue();
        int missingHp = Math.max(0, character.getMaxHp() - character.getHp());
        int restoredHp = Math.min(missingHp, healAmount);
        int overheal = healAmount - restoredHp;

        character.setHp(character.getHp() + restoredHp);
        character.setCoin(character.getCoin() + overheal);
    }
}
