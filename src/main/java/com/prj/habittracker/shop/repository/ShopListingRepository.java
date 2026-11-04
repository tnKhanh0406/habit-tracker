package com.prj.habittracker.shop.repository;

import com.prj.habittracker.shop.entity.ShopListingEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShopListingRepository extends JpaRepository<ShopListingEntity, UUID> {
    @EntityGraph(attributePaths = {"item", "item.equipmentDefinition", "item.consumableDefinition"})
    List<ShopListingEntity> findAllByEnabledTrueAndItemEnabledTrueOrderBySortOrderAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"item", "item.equipmentDefinition", "item.consumableDefinition"})
    @Query("SELECT listing FROM ShopListingEntity listing WHERE listing.id = :id")
    Optional<ShopListingEntity> findByIdForUpdate(UUID id);
}
