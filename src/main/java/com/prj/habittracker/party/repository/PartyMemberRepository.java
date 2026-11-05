package com.prj.habittracker.party.repository;

import com.prj.habittracker.party.entity.PartyMemberEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartyMemberRepository extends JpaRepository<PartyMemberEntity, UUID> {
    boolean existsByUserId(UUID userId);
    long countByPartyId(UUID partyId);

    @EntityGraph(attributePaths = {"party", "party.leader", "user"})
    @Query("SELECT member FROM PartyMemberEntity member WHERE member.user.id = :userId")
    Optional<PartyMemberEntity> findByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"party", "party.leader", "user"})
    @Query("SELECT member FROM PartyMemberEntity member WHERE member.user.id = :userId")
    Optional<PartyMemberEntity> findByUserIdForUpdate(UUID userId);

    @EntityGraph(attributePaths = "user")
    List<PartyMemberEntity> findAllByPartyIdOrderByJoinedAtAsc(UUID partyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = "user")
    @Query("SELECT member FROM PartyMemberEntity member WHERE member.party.id = :partyId AND member.user.id = :userId")
    Optional<PartyMemberEntity> findByPartyIdAndUserIdForUpdate(UUID partyId, UUID userId);

    void deleteAllByPartyId(UUID partyId);
}
