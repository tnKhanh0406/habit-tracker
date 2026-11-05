package com.prj.habittracker.party.service;

import com.prj.habittracker.party.dto.request.*;
import com.prj.habittracker.party.dto.response.PartyResponse;

import java.util.Optional;
import java.util.UUID;

public interface PartyService {
    Optional<PartyResponse> getCurrentParty();
    PartyResponse create(CreatePartyRequest request);
    PartyResponse join(JoinPartyRequest request);
    PartyResponse update(UpdatePartyRequest request);
    PartyResponse refreshInviteCode();
    PartyResponse kick(UUID memberUserId);
    PartyResponse transferLeadership(TransferLeadershipRequest request);
    void leave();
    void disband();
}
