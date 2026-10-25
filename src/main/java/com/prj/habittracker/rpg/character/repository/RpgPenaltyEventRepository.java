package com.prj.habittracker.rpg.character.repository;

import com.prj.habittracker.common.enums.PenaltySourceType;
import com.prj.habittracker.rpg.character.entity.RpgPenaltyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface RpgPenaltyEventRepository extends JpaRepository<RpgPenaltyEvent, UUID> {
    boolean existsBySourceTypeAndSourceId(PenaltySourceType sourceType, UUID sourceId);

    @Query("""
        SELECT event.sourceId
        FROM RpgPenaltyEvent event
        WHERE event.sourceType = :sourceType
        AND event.sourceId IN :sourceIds
    """)
    List<UUID> findExistingSourceIds(@Param("sourceType") PenaltySourceType sourceType,
                                     @Param("sourceIds") Collection<UUID> sourceIds);
}
