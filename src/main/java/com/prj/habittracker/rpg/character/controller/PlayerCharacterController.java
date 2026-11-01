package com.prj.habittracker.rpg.character.controller;

import com.prj.habittracker.rpg.character.dto.request.AllocateStatRequest;
import com.prj.habittracker.rpg.character.dto.request.SelectCharacterClassRequest;
import com.prj.habittracker.rpg.character.dto.response.AllocateStatResponse;
import com.prj.habittracker.rpg.character.dto.response.CharacterResponse;
import com.prj.habittracker.rpg.character.service.PlayerCharacterService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/character")
@RequiredArgsConstructor
public class PlayerCharacterController {
    private final PlayerCharacterService playerCharacterService;

    @GetMapping
    public CharacterResponse getCharacter() {
        return playerCharacterService.getCharacter();
    }

    @PostMapping("/allocate-stat")
    public AllocateStatResponse allocateStatResponse(@RequestBody AllocateStatRequest request) {
        return playerCharacterService.allocateStat(request);
    }

    @PostMapping("redeem-reward/{rewardId}")
    public long redeemReward(@PathVariable String rewardId) {
        return playerCharacterService.redeemReward(UUID.fromString(rewardId));
    }

    @PostMapping("/class")
    public CharacterResponse selectClass(@RequestBody @Valid SelectCharacterClassRequest request) {
        return playerCharacterService.selectClass(request);
    }
}
