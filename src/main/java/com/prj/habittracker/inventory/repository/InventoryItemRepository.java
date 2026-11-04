package com.prj.habittracker.inventory.repository;

import com.prj.habittracker.inventory.entity.InventoryItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItemEntity, UUID> {
    @EntityGraph(attributePaths = {"item", "item.equipmentDefinition", "item.consumableDefinition"})
    List<InventoryItemEntity> findAllByUserIdAndQuantityGreaterThanOrderByAcquiredAtAsc(UUID userId, int quantity);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"item", "item.equipmentDefinition", "item.consumableDefinition"})
    @Query("SELECT inventory FROM InventoryItemEntity inventory WHERE inventory.id = :id AND inventory.userId = :userId")
    Optional<InventoryItemEntity> findByIdAndUserIdForUpdate(UUID id, UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT inventory FROM InventoryItemEntity inventory WHERE inventory.userId = :userId AND inventory.item.id = :itemId")
    Optional<InventoryItemEntity> findByUserIdAndItemIdForUpdate(UUID userId, UUID itemId);
}
