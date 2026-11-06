package com.prj.habittracker.boss.service.impl;

import com.prj.habittracker.boss.entity.BossDamageEventEntity;
import com.prj.habittracker.boss.entity.PartyBossBattleEntity;
import com.prj.habittracker.boss.policy.BossDamagePolicy;
import com.prj.habittracker.boss.repository.BossDamageEventRepository;
import com.prj.habittracker.boss.repository.PartyBossBattleRepository;
import com.prj.habittracker.boss.service.BossDamageResult;
import com.prj.habittracker.boss.service.BossDamageService;
import com.prj.habittracker.boss.service.BossRewardService;
import com.prj.habittracker.common.enums.*;
import com.prj.habittracker.party.entity.PartyMemberEntity;
import com.prj.habittracker.party.repository.PartyMemberRepository;
import com.prj.habittracker.party.repository.PartyRepository;
import com.prj.habittracker.rpg.character.entity.PlayerCharacterEntity;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.service.CharacterStatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BossDamageServiceImpl implements BossDamageService {
    private final BossDamageEventRepository eventRepository;
    private final PartyBossBattleRepository battleRepository;
    private final PartyMemberRepository memberRepository;
    private final PartyRepository partyRepository;
    private final PlayerCharacterRepository characterRepository;
    private final CharacterStatService characterStatService;
    private final BossDamagePolicy damagePolicy;
    private final BossRewardService bossRewardService;

    @Override
    @Transactional
    public BossDamageResult applyCompletionDamage(UUID userId, RewardSourceType sourceType, UUID sourceId,
                                                  Difficulty difficulty) {
        Optional<BossDamageEventEntity> existing = eventRepository.findBySourceForUpdate(sourceType, sourceId);
        if (existing.isPresent()) {
            BossDamageEventEntity event = existing.get();
            requireOwner(event, userId);
            if (event.getStatus() == BossDamageStatus.APPLIED) return result(event.getBattle(), 0);
            PartyMemberEntity currentMembership = memberRepository.findByUserId(userId).orElse(null);
            if (currentMembership == null
                    || !currentMembership.getParty().getId().equals(event.getParty().getId())) {
                return BossDamageResult.noChange();
            }
            partyRepository.findByIdForUpdate(currentMembership.getParty().getId())
                    .orElseThrow(() -> new IllegalStateException("Party no longer exists"));
            PartyBossBattleEntity battle = battleRepository.findByIdForUpdate(event.getBattle().getId())
                    .orElseThrow(() -> new IllegalStateException("Boss battle no longer exists"));
            if (battle.getStatus() != BossBattleStatus.ACTIVE) return BossDamageResult.noChange();
            int reapplied = applyDamage(battle, event.getDamage());
            event.setDamage(reapplied);
            event.setStatus(BossDamageStatus.APPLIED);
            event.setReversedAt(null);
            eventRepository.flush();
            if (battle.getStatus() == BossBattleStatus.DEFEATED) {
                bossRewardService.grantDefeatRewards(battle);
            }
            return result(battle, -reapplied);
        }

        PartyMemberEntity membership = memberRepository.findByUserId(userId).orElse(null);
        if (membership == null || membership.getParty().getStatus() != PartyStatus.ACTIVE) {
            return BossDamageResult.noChange();
        }
        partyRepository.findByIdForUpdate(membership.getParty().getId())
                .orElseThrow(() -> new IllegalStateException("Party no longer exists"));
        PartyBossBattleEntity battle = battleRepository
                .findByPartyIdAndStatusForUpdate(membership.getParty().getId(), BossBattleStatus.ACTIVE)
                .orElse(null);
        if (battle == null) return BossDamageResult.noChange();

        PlayerCharacterEntity character = characterRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Player character not found"));
        var stats = characterStatService.calculate(character);
        int mainStat = damagePolicy.mainStat(character.getCharacterClass(), stats);
        int calculatedDamage = damagePolicy.calculate(sourceType, difficulty, mainStat, stats.bossDamagePercent());
        int actualDamage = applyDamage(battle, calculatedDamage);

        BossDamageEventEntity event = new BossDamageEventEntity();
        event.setBattle(battle);
        event.setParty(membership.getParty());
        event.setUser(membership.getUser());
        event.setSourceType(sourceType);
        event.setSourceId(sourceId);
        event.setDifficulty(difficulty);
        event.setBaseDamage(damagePolicy.baseDamage(sourceType));
        event.setMainStatSnapshot(mainStat);
        event.setBossDamagePercentSnapshot(stats.bossDamagePercent());
        event.setDamage(actualDamage);
        event.setStatus(BossDamageStatus.APPLIED);
        eventRepository.saveAndFlush(event);
        if (battle.getStatus() == BossBattleStatus.DEFEATED) {
            bossRewardService.grantDefeatRewards(battle);
        }
        return result(battle, -actualDamage);
    }

    @Override
    @Transactional
    public BossDamageResult reverseCompletionDamage(UUID userId, RewardSourceType sourceType, UUID sourceId) {
        BossDamageEventEntity event = eventRepository.findBySourceForUpdate(sourceType, sourceId).orElse(null);
        if (event == null || event.getStatus() == BossDamageStatus.REVERSED) return BossDamageResult.noChange();
        requireOwner(event, userId);
        PartyBossBattleEntity battle = battleRepository.findByIdForUpdate(event.getBattle().getId())
                .orElseThrow(() -> new IllegalStateException("Boss battle no longer exists"));

        int restoredHp = 0;
        if (battle.getStatus() == BossBattleStatus.ACTIVE) {
            int previousHp = battle.getCurrentHp();
            battle.setCurrentHp(Math.min(battle.getMaxHp(), previousHp + event.getDamage()));
            restoredHp = battle.getCurrentHp() - previousHp;
        }
        event.setStatus(BossDamageStatus.REVERSED);
        event.setReversedAt(OffsetDateTime.now());
        return result(battle, restoredHp);
    }

    private int applyDamage(PartyBossBattleEntity battle, int requestedDamage) {
        int actualDamage = Math.min(battle.getCurrentHp(), requestedDamage);
        battle.setCurrentHp(battle.getCurrentHp() - actualDamage);
        if (battle.getCurrentHp() == 0) {
            battle.setStatus(BossBattleStatus.DEFEATED);
            battle.setDefeatedAt(OffsetDateTime.now());
        }
        return actualDamage;
    }

    private void requireOwner(BossDamageEventEntity event, UUID userId) {
        if (!event.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Completion damage belongs to another user");
        }
    }

    private BossDamageResult result(PartyBossBattleEntity battle, int damageDelta) {
        return new BossDamageResult(battle.getId(), damageDelta, battle.getCurrentHp(), battle.getStatus());
    }
}
