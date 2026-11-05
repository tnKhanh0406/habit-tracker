package com.prj.habittracker.boss.repository;

import com.prj.habittracker.boss.entity.BossDefinitionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BossDefinitionRepository extends JpaRepository<BossDefinitionEntity, UUID> {
    List<BossDefinitionEntity> findAllByEnabledTrueOrderBySequenceOrderAsc();
    Optional<BossDefinitionEntity> findByIdAndEnabledTrue(UUID id);
    Optional<BossDefinitionEntity> findBySequenceOrderAndEnabledTrue(int sequenceOrder);
}
