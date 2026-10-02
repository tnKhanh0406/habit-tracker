package com.prj.habittracker.rpg.character.repository;

import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacterEntity, UUID> {
    Optional<PlayerCharacterEntity> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
}
