package com.prj.habittracker.shop.repository;

import com.prj.habittracker.shop.entity.ShopPurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ShopPurchaseRepository extends JpaRepository<ShopPurchaseEntity, UUID> {
    Optional<ShopPurchaseEntity> findByUserIdAndIdempotencyKey(UUID userId, UUID idempotencyKey);
}
