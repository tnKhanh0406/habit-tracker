package com.prj.habittracker.boss.service.impl;

import com.prj.habittracker.boss.entity.BossRewardGrantEntity;
import com.prj.habittracker.boss.entity.PartyBossBattleEntity;
import com.prj.habittracker.boss.repository.*;
import com.prj.habittracker.boss.service.BossRewardService;
import com.prj.habittracker.common.enums.BossBattleStatus;
import com.prj.habittracker.common.enums.BossDamageStatus;
import com.prj.habittracker.common.enums.BossRewardType;
import com.prj.habittracker.party.repository.PartyMemberRepository;
import com.prj.habittracker.rpg.character.repository.PlayerCharacterRepository;
import com.prj.habittracker.rpg.character.service.CharacterProgressionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BossRewardServiceImpl implements BossRewardService {
    private final BossRewardRepository rewardRepository;
    private final BossRewardGrantRepository grantRepository;
    private final BossDamageEventRepository damageEventRepository;
    private final PartyMemberRepository memberRepository;
    private final PlayerCharacterRepository characterRepository;
    private final CharacterProgressionService progressionService;

    @Override
    @Transactional
    public void grantDefeatRewards(PartyBossBattleEntity battle) {
        if (battle.getStatus() != BossBattleStatus.DEFEATED) return;

        Map<BossRewardType, Long> rewards = rewardRepository
                .findAllByBossDefinitionIdOrderBySortOrderAsc(battle.getBossDefinition().getId()).stream()
                .collect(Collectors.toMap(reward -> reward.getRewardType(), reward -> reward.getAmount()));
        long exp = rewards.getOrDefault(BossRewardType.EXP, 0L);
        long coin = rewards.getOrDefault(BossRewardType.COIN, 0L);
        long gem = rewards.getOrDefault(BossRewardType.GEM, 0L);

        var currentMembers = memberRepository.findAllByPartyIdOrderByJoinedAtAsc(battle.getParty().getId()).stream()
                .collect(Collectors.toMap(member -> member.getUser().getId(), member -> member.getUser()));
        List<BossDamageEventRepository.ContributorDamage> eligible = damageEventRepository
                .findContributors(battle.getId(), BossDamageStatus.APPLIED).stream()
                .filter(contributor -> currentMembers.containsKey(contributor.getUserId()))
                .sorted(Comparator.comparing(contributor -> contributor.getUserId().toString()))
                .toList();

        for (BossDamageEventRepository.ContributorDamage contributor : eligible) {
            UUID userId = contributor.getUserId();
            if (grantRepository.existsByBattleIdAndUserId(battle.getId(), userId)) continue;
            var character = characterRepository.findByUserIdForUpdate(userId)
                    .orElseThrow(() -> new IllegalStateException("Eligible character not found"));
            character.setGem(Math.addExact(character.getGem(), gem));
            progressionService.applyReward(userId, exp, coin);

            BossRewardGrantEntity grant = new BossRewardGrantEntity();
            grant.setBattle(battle);
            grant.setParty(battle.getParty());
            grant.setUser(currentMembers.get(userId));
            grant.setExpAmount(exp);
            grant.setCoinAmount(coin);
            grant.setGemAmount(gem);
            grant.setDamageContribution(contributor.getTotalDamage());
            grantRepository.save(grant);
        }
    }
}
