package com.prj.habittracker.rpg.skill.repository;

import com.prj.habittracker.common.enums.CharacterClass;
import com.prj.habittracker.rpg.skill.entity.SkillEntity;
import com.prj.habittracker.common.enums.SkillType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<SkillEntity, UUID> {
    @EntityGraph(attributePaths = "effects")
    List<SkillEntity> findAllByCharacterClassAndEnabledTrue(CharacterClass characterClass);

    @EntityGraph(attributePaths = "effects")
    Optional<SkillEntity> findByIdAndEnabledTrue(UUID id);

    @EntityGraph(attributePaths = "effects")
    List<SkillEntity> findAllByCharacterClassAndSkillTypeAndEnabledTrue(CharacterClass characterClass, SkillType skillType);
}
