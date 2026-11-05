package com.prj.habittracker.party.repository;

import com.prj.habittracker.party.entity.PartyEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PartyRepository extends JpaRepository<PartyEntity, UUID> {
    boolean existsByInviteCode(String inviteCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "leader")
    @Query("SELECT party FROM PartyEntity party WHERE party.inviteCode = :inviteCode")
    Optional<PartyEntity> findByInviteCodeForUpdate(String inviteCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "leader")
    @Query("SELECT party FROM PartyEntity party WHERE party.id = :id")
    Optional<PartyEntity> findByIdForUpdate(UUID id);
}
