package com.prj.habittracker.inventory.repository;

import com.prj.habittracker.inventory.entity.InventoryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransactionEntity, UUID> {}
