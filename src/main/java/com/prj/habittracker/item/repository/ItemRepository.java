package com.prj.habittracker.item.repository;

import com.prj.habittracker.item.entity.ItemEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
    @EntityGraph(attributePaths = {"equipmentDefinition", "consumableDefinition"})
    Optional<ItemEntity> findByCodeAndEnabledTrue(String code);

    @EntityGraph(attributePaths = {"equipmentDefinition", "consumableDefinition"})
    List<ItemEntity> findAllByCodeInAndEnabledTrue(Collection<String> codes);
}
