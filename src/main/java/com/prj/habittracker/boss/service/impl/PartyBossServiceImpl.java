package com.prj.habittracker.boss.service.impl;

import com.prj.habittracker.boss.dto.request.StartBossBattleRequest;
import com.prj.habittracker.boss.dto.response.BossBattleResponse;
import com.prj.habittracker.boss.dto.response.BossCatalogResponse;
import com.prj.habittracker.boss.entity.BossDefinitionEntity;
import com.prj.habittracker.boss.entity.PartyBossBattleEntity;
import com.prj.habittracker.boss.repository.BossDefinitionRepository;
import com.prj.habittracker.boss.repository.PartyBossBattleRepository;
import com.prj.habittracker.boss.service.PartyBossService;
import com.prj.habittracker.common.enums.*;
import com.prj.habittracker.common.security.CurrentUserService;
import com.prj.habittracker.party.entity.PartyEntity;
import com.prj.habittracker.party.entity.PartyMemberEntity;
import com.prj.habittracker.party.repository.PartyMemberRepository;
import com.prj.habittracker.party.repository.PartyRepository;
import com.prj.habittracker.user.entity.UserEntity;
import com.prj.habittracker.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartyBossServiceImpl implements PartyBossService {
    private final BossDefinitionRepository bossRepository;
    private final PartyBossBattleRepository battleRepository;
    private final PartyMemberRepository memberRepository;
    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<BossCatalogResponse> getCatalog() {
        PartyMemberEntity membership = currentMembership();
        UUID partyId = membership.getParty().getId();
        Map<UUID, PartyBossBattleEntity> battles = battleRepository.findAllByPartyId(partyId).stream()
                .collect(Collectors.toMap(battle -> battle.getBossDefinition().getId(), Function.identity()));
        boolean hasActive = battles.values().stream().anyMatch(b -> b.getStatus() == BossBattleStatus.ACTIVE);
        List<BossDefinitionEntity> definitions = bossRepository.findAllByEnabledTrueOrderBySequenceOrderAsc();

        return definitions.stream().map(boss -> {
            PartyBossBattleEntity battle = battles.get(boss.getId());
            BossAvailability availability;
            String reason = null;
            if (battle != null && battle.getStatus() == BossBattleStatus.DEFEATED) {
                availability = BossAvailability.DEFEATED;
            } else if (battle != null && battle.getStatus() == BossBattleStatus.ACTIVE) {
                availability = BossAvailability.ACTIVE;
            } else if (hasActive) {
                availability = BossAvailability.LOCKED;
                reason = "Finish the active battle first";
            } else if (boss.getSequenceOrder() == 1 || previousBossDefeated(definitions, battles, boss)) {
                availability = BossAvailability.AVAILABLE;
            } else {
                availability = BossAvailability.LOCKED;
                reason = "Defeat the previous boss to unlock";
            }
            return new BossCatalogResponse(boss.getId(), boss.getCode(), boss.getName(), boss.getDescription(),
                    boss.getDifficulty(), boss.getSequenceOrder(), boss.getBaseHp(), boss.getIcon(), availability, reason);
        }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BossBattleResponse> getActiveBattle() {
        UUID partyId = currentMembership().getParty().getId();
        return battleRepository.findByPartyIdAndStatus(partyId, BossBattleStatus.ACTIVE).map(this::toResponse);
    }

    @Override
    @Transactional
    public BossBattleResponse startBattle(StartBossBattleRequest request) {
        LockedParty context = lockCurrentParty();
        if (context.membership().getRole() != PartyRole.LEADER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the party leader can start a boss battle");
        }
        if (battleRepository.findByPartyIdAndStatusForUpdate(context.party().getId(), BossBattleStatus.ACTIVE).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Party already has an active boss battle");
        }
        BossDefinitionEntity boss = bossRepository.findByIdAndEnabledTrue(request.bossDefinitionId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boss not found"));
        if (battleRepository.existsByPartyIdAndBossDefinitionIdAndStatus(context.party().getId(), boss.getId(),
                BossBattleStatus.DEFEATED)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This boss has already been defeated");
        }
        if (boss.getSequenceOrder() > 1) {
            BossDefinitionEntity previous = bossRepository
                    .findBySequenceOrderAndEnabledTrue(boss.getSequenceOrder() - 1)
                    .orElseThrow(() -> new IllegalStateException("Previous boss definition is missing"));
            boolean unlocked = battleRepository.existsByPartyIdAndBossDefinitionIdAndStatus(context.party().getId(),
                    previous.getId(), BossBattleStatus.DEFEATED);
            if (!unlocked) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Defeat the previous boss first");
            }
        }

        PartyBossBattleEntity battle = new PartyBossBattleEntity();
        battle.setParty(context.party());
        battle.setBossDefinition(boss);
        battle.setStartedBy(context.user());
        battle.setStatus(BossBattleStatus.ACTIVE);
        battle.setMaxHp(boss.getBaseHp());
        battle.setCurrentHp(boss.getBaseHp());
        return toResponse(battleRepository.saveAndFlush(battle));
    }

    private boolean previousBossDefeated(List<BossDefinitionEntity> definitions,
                                         Map<UUID, PartyBossBattleEntity> battles,
                                         BossDefinitionEntity boss) {
        return definitions.stream()
                .filter(candidate -> candidate.getSequenceOrder() == boss.getSequenceOrder() - 1)
                .findFirst()
                .map(previous -> battles.get(previous.getId()))
                .map(battle -> battle.getStatus() == BossBattleStatus.DEFEATED)
                .orElse(false);
    }

    private PartyMemberEntity currentMembership() {
        return memberRepository.findByUserId(currentUserService.getCurrentUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "You are not in a party"));
    }

    private LockedParty lockCurrentParty() {
        UUID userId = currentUserService.getCurrentUserId();
        UserEntity user = userRepository.findByIdForUpdate(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        PartyMemberEntity snapshot = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "You are not in a party"));
        PartyEntity party = partyRepository.findByIdForUpdate(snapshot.getParty().getId())
                .orElseThrow(() -> new EntityNotFoundException("Party not found"));
        PartyMemberEntity membership = memberRepository.findByUserIdForUpdate(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "You are not in a party"));
        if (party.getStatus() != PartyStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Party is no longer active");
        }
        return new LockedParty(user, membership, party);
    }

    private BossBattleResponse toResponse(PartyBossBattleEntity battle) {
        return new BossBattleResponse(battle.getId(), battle.getParty().getId(), battle.getBossDefinition().getId(),
                battle.getBossDefinition().getCode(), battle.getBossDefinition().getName(),
                battle.getBossDefinition().getDifficulty(), battle.getStatus(), battle.getCurrentHp(), battle.getMaxHp(),
                battle.getStartedBy().getId(), battle.getStartedBy().getUsername(), battle.getStartedAt());
    }

    private record LockedParty(UserEntity user, PartyMemberEntity membership, PartyEntity party) {}
}
