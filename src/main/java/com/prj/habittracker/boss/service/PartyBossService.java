package com.prj.habittracker.boss.service;

import com.prj.habittracker.boss.dto.request.StartBossBattleRequest;
import com.prj.habittracker.boss.dto.response.BossBattleResponse;
import com.prj.habittracker.boss.dto.response.BossCatalogResponse;

import java.util.List;
import java.util.Optional;

public interface PartyBossService {
    List<BossCatalogResponse> getCatalog();
    Optional<BossBattleResponse> getActiveBattle();
    BossBattleResponse startBattle(StartBossBattleRequest request);
}
