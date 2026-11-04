package com.prj.habittracker.inventory.repository;

import com.prj.habittracker.common.enums.EquipmentSlot;
import com.prj.habittracker.inventory.entity.EquippedItemEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EquippedItemRepository extends JpaRepository<EquippedItemEntity, UUID> {
    @EntityGraph(attributePaths = {"inventoryItem", "inventoryItem.item", "inventoryItem.item.equipmentDefinition"})
    List<EquippedItemEntity> findAllByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"inventoryItem", "inventoryItem.item", "inventoryItem.item.equipmentDefinition"})
    @Query("SELECT equipped FROM EquippedItemEntity equipped WHERE equipped.userId = :userId AND equipped.slot = :slot")
    Optional<EquippedItemEntity> findByUserIdAndSlotForUpdate(UUID userId, EquipmentSlot slot);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"inventoryItem", "inventoryItem.item", "inventoryItem.item.equipmentDefinition"})
    @Query("SELECT equipped FROM EquippedItemEntity equipped WHERE equipped.userId = :userId AND equipped.inventoryItem.id = :inventoryItemId")
    Optional<EquippedItemEntity> findByUserIdAndInventoryItemIdForUpdate(UUID userId, UUID inventoryItemId);
}
