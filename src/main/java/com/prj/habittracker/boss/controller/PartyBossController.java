package com.prj.habittracker.boss.controller;

import com.prj.habittracker.boss.dto.request.StartBossBattleRequest;
import com.prj.habittracker.boss.dto.response.BossBattleResponse;
import com.prj.habittracker.boss.dto.response.BossCatalogResponse;
import com.prj.habittracker.boss.service.PartyBossService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/party")
@RequiredArgsConstructor
public class PartyBossController {
    private final PartyBossService partyBossService;

    @GetMapping("/bosses")
    public List<BossCatalogResponse> getCatalog() {
        return partyBossService.getCatalog();
    }

    @GetMapping("/boss-battles/active")
    public ResponseEntity<BossBattleResponse> getActiveBattle() {
        return partyBossService.getActiveBattle().map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/boss-battles")
    public BossBattleResponse startBattle(@Valid @RequestBody StartBossBattleRequest request) {
        return partyBossService.startBattle(request);
    }
}
