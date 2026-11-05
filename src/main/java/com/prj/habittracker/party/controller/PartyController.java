package com.prj.habittracker.party.controller;

import com.prj.habittracker.party.dto.request.*;
import com.prj.habittracker.party.dto.response.PartyResponse;
import com.prj.habittracker.party.service.PartyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/parties")
@RequiredArgsConstructor
public class PartyController {
    private final PartyService partyService;

    @GetMapping("/me")
    public ResponseEntity<PartyResponse> getCurrentParty() {
        return partyService.getCurrentParty().map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping
    public PartyResponse create(@Valid @RequestBody CreatePartyRequest request) {
        return partyService.create(request);
    }

    @PostMapping("/join")
    public PartyResponse join(@Valid @RequestBody JoinPartyRequest request) {
        return partyService.join(request);
    }

    @PatchMapping("/me")
    public PartyResponse update(@Valid @RequestBody UpdatePartyRequest request) {
        return partyService.update(request);
    }

    @PostMapping("/invite-code/refresh")
    public PartyResponse refreshInviteCode() {
        return partyService.refreshInviteCode();
    }

    @DeleteMapping("/members/{userId}")
    public PartyResponse kick(@PathVariable UUID userId) {
        return partyService.kick(userId);
    }

    @PostMapping("/transfer-leadership")
    public PartyResponse transferLeadership(@Valid @RequestBody TransferLeadershipRequest request) {
        return partyService.transferLeadership(request);
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leave() {
        partyService.leave();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> disband() {
        partyService.disband();
        return ResponseEntity.noContent().build();
    }
}
