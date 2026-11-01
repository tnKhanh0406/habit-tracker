package com.prj.habittracker.rpg.character.repository;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacterEntity, UUID> {
    Optional<PlayerCharacterEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT character FROM PlayerCharacterEntity character WHERE character.userId = :userId")
    Optional<PlayerCharacterEntity> findByUserIdForUpdate(UUID userId);
}
