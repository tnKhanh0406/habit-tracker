package com.prj.habittracker.boss.repository;

import com.prj.habittracker.boss.entity.PartyBossBattleEntity;
import com.prj.habittracker.common.enums.BossBattleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartyBossBattleRepository extends JpaRepository<PartyBossBattleEntity, UUID> {
    @EntityGraph(attributePaths = {"bossDefinition", "startedBy"})
    List<PartyBossBattleEntity> findAllByPartyId(UUID partyId);

    @EntityGraph(attributePaths = {"bossDefinition", "startedBy"})
    Optional<PartyBossBattleEntity> findByPartyIdAndStatus(UUID partyId, BossBattleStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"bossDefinition", "startedBy"})
    @Query("SELECT battle FROM PartyBossBattleEntity battle WHERE battle.party.id = :partyId AND battle.status = :status")
    Optional<PartyBossBattleEntity> findByPartyIdAndStatusForUpdate(UUID partyId, BossBattleStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"bossDefinition", "startedBy", "party"})
    @Query("SELECT battle FROM PartyBossBattleEntity battle WHERE battle.id = :id")
    Optional<PartyBossBattleEntity> findByIdForUpdate(UUID id);

    boolean existsByPartyIdAndBossDefinitionIdAndStatus(UUID partyId, UUID bossDefinitionId,
                                                         BossBattleStatus status);
}
