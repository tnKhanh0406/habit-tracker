package com.prj.habittracker.party.dto.response;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PartyResponse(
        UUID id,
        String name,
        String inviteCode,
        int maxMembers,
        int memberCount,
        UUID leaderUserId,
        boolean currentUserLeader,
        OffsetDateTime createdAt,
        List<PartyMemberResponse> members
) {}
