package com.prj.habittracker.boss.service;

import com.prj.habittracker.boss.entity.PartyBossBattleEntity;

public interface BossRewardService {
    void grantDefeatRewards(PartyBossBattleEntity battle);
}
